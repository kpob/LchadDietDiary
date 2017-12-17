package pl.kpob.dietdiary.domain

import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.db.IngredientCategory

/**
 * Created by kpob on 21.10.2017.
 */
data class Meal(
        val id: String,
        val time: String,
        val date: String,
        val type: MealType,
        val calories: Float,
        val lct: Float,
        val dayOfYear: Int,
        val year: Int,
        val timestamp: Long,
        val isToday: Boolean
)

data class MealDetails(
        val time: String,
        val date: String,
        val type: MealType,
        val caloriesTotal: Float,
        val mtc: Float,
        val lct: Float,
        val carbohydrates: Float,
        val protein: Float,
        val salt: Float,
        val roughage: Float,
        val ingredients: List<MealIngredient>
)

data class MealIngredient(
        val id: String,
        val name: String,
        val calories: Float,
        val weight: Float
)

enum class MealType(val string: String, val icon: Int, val filters: List<IngredientCategory>) {
    MILK("Mleczko", R.drawable.ic_milk_bottle, listOf(IngredientCategory.OTHERS)),
    DESSERT("Deserek", R.drawable.ic_porridge, listOf(IngredientCategory.OILS, IngredientCategory.PORRIDGE, IngredientCategory.DIARY, IngredientCategory.FRUITS, IngredientCategory.FRUITS_TUBE)),
    DINNER("Obiadek", R.drawable.ic_dinner, listOf(IngredientCategory.OILS, IngredientCategory.PORRIDGE, IngredientCategory.DINNERS, IngredientCategory.DIARY, IngredientCategory.FRUITS, IngredientCategory.FRUITS_TUBE)),
    OTHER("Przekąska", R.drawable.ic_milk_bottle, listOf(IngredientCategory.OTHERS));

    companion object {
        fun fromString(s: String) = values().firstOrNull { it.name == s } ?: OTHER
    }
}