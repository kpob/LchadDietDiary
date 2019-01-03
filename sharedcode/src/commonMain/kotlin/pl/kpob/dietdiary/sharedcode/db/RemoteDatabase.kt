package pl.kpob.dietdiary.sharedcode.db

import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.FbMeal
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.Meal

interface RemoteDatabase {

    fun saveIngredients(data: List<FbIngredient>)
    fun saveIngredient(item: FbIngredient, update: Boolean = false)

    fun updateUsageCounter(itemId: String, value: Int)

    fun saveMeal(meal: FbMeal, update: Boolean = false)
    fun removeMeal(item: Meal)
    fun updateMealTime(mealId: String, time: Long)

    fun deleteIngredient(item: Ingredient)
    fun addToken(token: String)
}