package pl.kpob.dietdiary.repo

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import pl.kpob.dietdiary.db.RealmDatabase
import pl.kpob.dietdiary.sharedcode.repository.*

/**
 * Created by kpob on 11.12.2017.
 */

class RealmAddTransaction<T>: AddTransaction<T> where T: RealmObject {

    override fun execute(input: T, db: Database<T>) {
        (db as? RealmDatabase<T>)?.realm?.insertOrUpdate(input)
    }

    override fun execute(input: Collection<T>, db: Database<T>) {
        (db as? RealmDatabase<T>)?.realm?.insertOrUpdate(input)
    }

}

open class RealmUpdateTransaction<T: RealmObject> private constructor(private val update: (T) -> Unit): UpdateTransaction<T> {

    override fun execute(input: Collection<T>, db: Database<T>) {
        input.forEach { update.invoke(it) }
    }

    override fun execute(input: T, db: Database<T>) {
        update.invoke(input)
    }

}

open class RealmRemoveTransaction<T: RealmObject>: RemoveTransaction<T> {

    override fun execute(input: Collection<T>, db: Database<T>) {
        when(input) {
            is RealmResults<T> -> input.deleteAllFromRealm()
            else -> input.forEach { it.deleteFromRealm() }
        }
    }

    override fun execute(input: T, db: Database<T>) {
        input.deleteFromRealm()
    }

}

class RealmChainTransaction<T: RealmObject>(override val data: List<ChainTransactionData<T>>): ChainTransaction<T> {

    override fun execute(db: Database<T>) {
        val realm: Realm = (db as? RealmDatabase<T>)?.realm ?: return
        realm.executeTransaction {r ->
            data.forEach {cdt ->
                val itemDb = RealmDatabase(r, db.clazz)
                if (cdt.data != null) {
                    cdt.transaction.execute(cdt.data!!, itemDb)
                }
                if (cdt.item != null) {
                    cdt.transaction.execute(cdt.item!!, itemDb)
                }
            }
        }
    }

    companion object {
        fun<T: RealmObject> of(vararg data: ChainTransactionData<T>): ChainTransaction<T> {
            return RealmChainTransaction(data.toList())
        }
    }




}