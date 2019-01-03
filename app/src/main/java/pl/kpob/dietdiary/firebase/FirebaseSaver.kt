package pl.kpob.dietdiary.firebase

import com.google.firebase.database.DatabaseReference
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.FbMeal
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.Meal

/**
 * Created by kpob on 27.10.2017.
 */
class FirebaseSaver: RemoteDatabase {


    private val db: DatabaseReference get() = firebaseDb

    override fun saveIngredients(data: List<FbIngredient>) = withDb { db ->
        data.forEach { db.addIngredient(it) }
    }

    override fun saveIngredient(item: FbIngredient, update: Boolean) = withDb { db ->
        db.addIngredient(item, update)
    }

    override fun updateUsageCounter(itemId: String, value: Int) = withDb { db ->
        db.increaseIngredientUsage(itemId, value)
    }

    override fun saveMeal(meal: FbMeal, update: Boolean) = withDb { db ->
        db.addMeal(meal, update)
    }

    override fun removeMeal(item: Meal) {
        db.mealsRef.child(item.id).updateChildren(mapOf("deleted" to true))
    }

    override fun deleteIngredient(item: Ingredient) {
        db.ingredientsRef.child(item.id).updateChildren(mapOf("deleted" to true))
    }

    override fun updateMealTime(mealId: String, time: Long) {
        db.mealsRef.child(mealId).updateChildren(mapOf("time" to time))
    }

    override fun addToken(token: String) {
        db.addToken(token)
    }

    private inline fun withDb(f: (DatabaseReference) -> Unit) {
        f(db)
    }

}