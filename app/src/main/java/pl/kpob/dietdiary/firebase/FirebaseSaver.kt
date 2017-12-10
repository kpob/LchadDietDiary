package pl.kpob.dietdiary.firebase

import com.google.firebase.database.DatabaseReference
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.server.FbIngredient
import pl.kpob.dietdiary.server.FbMeal

/**
 * Created by kpob on 27.10.2017.
 */
class FirebaseSaver {

    val db: DatabaseReference get() = firebaseDb

    fun saveIngredients(data: List<FbIngredient>) = withDb { db ->
        data.forEach { db.addIngredient(it) }
    }

    fun saveIngredient(item: FbIngredient, update: Boolean = false) = withDb { db ->
        db.addIngredient(item, update)
    }

    fun updateUsageCounter(itemId: String, value: Int) = withDb { db ->
        db.increaseIngredientUsage(itemId, value)
    }


    fun saveMeal(meal: FbMeal, update: Boolean = false) = withDb { db ->
        db.addMeal(meal, update)
    }

    private inline fun withDb(f: (DatabaseReference) -> Unit) {
        f(db)
    }

    fun removeMeal(item: Meal) {
        db.mealsRef.child(item.id).updateChildren(mapOf("deleted" to true))
    }

    fun updateMealTime(mealId: String, time: Long) {
        db.mealsRef.child(mealId).updateChildren(mapOf("time" to time))
    }

}