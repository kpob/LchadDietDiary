package pl.kpob.dietdiary

import pl.kpob.dietdiary.db.IngredientCategory
import pl.kpob.dietdiary.server.FbIngredient

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

data class Ingredient(
        val id: String,
        val name: String,
        val mct: Float,
        val lct: Float,
        val carbohydrates: Float,
        val protein: Float,
        val salt: Float,
        val roughage: Float,
        val calories: Float,
        val category: Int,
        val useCount: Int
) {
    override fun toString(): String = name

    fun toFirebase(deleted: Boolean = false): FbIngredient = FbIngredient(
            id, name, mct, lct, carbohydrates, protein, salt, roughage, calories, category, deleted
    )
}

enum class MealType(val string: String, val icon: Int, val filters: List<IngredientCategory>) {
    MILK("Mleczko", R.drawable.ic_milk_bottle, listOf(IngredientCategory.OTHERS)),
    DESSERT("Deserek", R.drawable.ic_porridge, listOf(IngredientCategory.OILS, IngredientCategory.PORRIDGE, IngredientCategory.DIARY, IngredientCategory.FRUITS, IngredientCategory.FRUITS_TUBE)),
    DINNER("Obiadek", R.drawable.ic_dinner, listOf(IngredientCategory.OILS, IngredientCategory.PORRIDGE, IngredientCategory.DINNERS, IngredientCategory.DIARY, IngredientCategory.FRUITS, IngredientCategory.FRUITS_TUBE)),
    OTHER("Przekąska", R.drawable.ic_milk_bottle, listOf(IngredientCategory.OTHERS));

    companion object {
        fun fromString(s: String) = values().firstOrNull { it.name == s } ?: OTHER
    }
}