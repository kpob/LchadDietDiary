package pl.kpob.dietdiary.repo

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Created by kpob on 11.12.2017.
 */
interface Transaction<in T> {

    fun execute(input: T)
    fun execute(input: Collection<T>)
}

interface AddTransaction<in T>: Transaction<T>
interface UpdateTransaction<in T>: Transaction<T>
interface RemoveTransaction<in T>: Transaction<T>


class RealmAddTransaction<in T: RealmObject>(val realm: Realm): AddTransaction<T> {

    override fun execute(input: T) {
        realm.insertOrUpdate(input)
    }

    override fun execute(input: Collection<T>) {
        realm.insertOrUpdate(input)
    }

}


open class RealmUpdateTransaction<in T: RealmObject>
private constructor(private val update: (T) -> Unit): UpdateTransaction<T> {
    override fun execute(input: Collection<T>) {
        input.forEach { update.invoke(it) }
    }

    override fun execute(input: T) {
        update.invoke(input)
    }

}

open class RealmRemoveTransaction<in T: RealmObject>: RemoveTransaction<T> {

    override fun execute(input: Collection<T>) {
        when(input) {
            is RealmResults<T> -> input.deleteAllFromRealm()
            else -> input.forEach { it.deleteFromRealm() }
        }
    }

    override fun execute(input: T) {
        input.deleteFromRealm()
    }

}