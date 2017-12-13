package pl.kpob.dietdiary.repo

import io.realm.Realm
import io.realm.RealmObject
import pl.kpob.dietdiary.Ingredient
import pl.kpob.dietdiary.Meal
import pl.kpob.dietdiary.MealDetails
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.mapper.IngredientMapper
import pl.kpob.dietdiary.mapper.MealDetailsMapper
import pl.kpob.dietdiary.mapper.MealMapper
import pl.kpob.dietdiary.usingRealm

/**
 * Created by kpob on 22.10.2017.
 */
interface Repository<In, out Out> {

    val mapper: Mapper<In, Out>

    fun query(spec: Specification<In>): List<Out> = mapper.map(spec.collection)
    fun querySingle(spec: Specification<In>): Out? {
        val item: In = spec.single ?: return null
        return mapper.map(item)
    }
    fun update(spec: Specification<In>, transaction: Transaction<In>) = transaction.execute(spec.collection)
    fun insert(item: In, transaction: AddTransaction<In>) =  transaction.execute(item)
    fun insert(list: List<In>, transaction: AddTransaction<In>) = transaction.execute(list)
    fun delete(spec: Specification<In>, transaction: RemoveTransaction<In>) = transaction.execute(spec.collection)
}

abstract class RealmRepository<T: RealmObject, V>(override val mapper: Mapper<T, V>): Repository<T, V> {

    inline fun<R> withRealm(crossinline f: RealmRepository<T, V>.(Realm) -> R): R = usingRealm { f(it) }
    inline fun withRealmQuery(crossinline spec: (Realm) -> Specification<T>): List<V>  = usingRealm { query(spec(it)) }
    inline fun withRealmSingleQuery(crossinline spec: (Realm) -> Specification<T>): V?  = usingRealm { querySingle(spec(it)) }

}



class MealRepository: RealmRepository<MealDTO, Meal>(MealMapper)
class MealDetailsRepository: RealmRepository<MealDTO, MealDetails>(MealDetailsMapper())
class IngredientRepository: RealmRepository<IngredientDTO, Ingredient>(IngredientMapper())

