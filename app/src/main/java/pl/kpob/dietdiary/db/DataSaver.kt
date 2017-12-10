package pl.kpob.dietdiary.db

import io.realm.Realm
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import pl.kpob.dietdiary.server.FbIngredient
import pl.kpob.dietdiary.server.FbMeal
import pl.kpob.dietdiary.server.FbMealIngredient
import pl.kpob.dietdiary.usingRealm

/**
 * Created by kpob on 27.10.2017.
 */
object DataSaver {

    fun saveIngredients(data: List<FbIngredient>, callback: (Boolean) -> Unit = {}) = executeAsync(callback) {
        it.insertOrUpdate(data.map { it.toRealm() })
    }

    fun saveIngredient(item:  FbIngredient, callback: (Boolean) -> Unit = {}) = executeAsync(callback) {
        it.insertOrUpdate(item.toRealm())
    }

    fun updateIngredients(data: List<FbIngredient>, callback: (Boolean) -> Unit = {}) = executeAsync(callback) {
        val ingredientsToDelete = data.filter { it.deleted }
        val ingredientsToSave = data.subtract(ingredientsToDelete).map { it.toRealm() }
        it.insertOrUpdate(ingredientsToSave)

        if (ingredientsToDelete.isEmpty()) {
            it.where(IngredientDTO::class.java)
                    .`in`("id", ingredientsToDelete.map { it.id }.toTypedArray())
                    .findAll()
                    .deleteAllFromRealm()
        }
    }


    fun incrementIngredientsUseCount(data: List<FbMealIngredient>) = executeAsync({}) {
        it.where(IngredientDTO::class.java)
                .`in`("id", data.map { it.ingredientId }.toTypedArray())
                .findAll()
                .map {
                    it.useCount = it.useCount + 1
                }
    }

    fun updateMeals(data: List<FbMeal>, callback: (Boolean) -> Unit) = executeAsync(callback) {
        val mealsToDelete = data.filter { it.deleted }
        val mealsToSave = data.subtract(mealsToDelete).map { it.toRealm() }
        it.insertOrUpdate(mealsToSave)
        if (mealsToDelete.isNotEmpty()) {
            it.where(MealDTO::class.java)
                    .`in`("id", mealsToDelete.map { it.id }.toTypedArray())
                    .findAll()
                    .deleteAllFromRealm()
        }
    }

    private inline fun executeAsync(crossinline cb: (Boolean) -> Unit, crossinline f: (Realm) -> Unit) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync(
                {
                    f(it)
                },
                {
                    cb(true)
                    realm.close()
                },
                {
                    cb(false)
                    realm.close()
                })
    }

    fun saveMeal(meal: FbMeal, callback: (Boolean) -> Unit) = executeAsync(callback) {
        it.insertOrUpdate(meal.toRealm())
    }

}