package pl.kpob.dietdiary.domain

import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.db.IngredientCategory

/**
 * Created by kpob on 21.10.2017.
 */
data class Credentials(val login: String, val password: String)

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
        val weight: Float = 0f
)

data class MealTemplate(
        val id: String,
        val name: String,
        val type: MealType,
        val ingredients: List<Ingredient>
)

data class IngredientUsage(val itemId: String, val counter: Int)
data class MealPart(val ingredient: Ingredient, val weight: Float) {
    val kcal: Float = ingredient.calories * weight/100f
}

enum class MealType(val string: String, val icon: Int, val filters: List<IngredientCategory>) {
    MILK("Mleczko", R.drawable.ic_milk_bottle, listOf(IngredientCategory.OTHERS)),
    DESSERT("Deserek", R.drawable.ic_porridge, listOf(IngredientCategory.OILS, IngredientCategory.PORRIDGE, IngredientCategory.DIARY, IngredientCategory.FRUITS, IngredientCategory.FRUITS_TUBE)),
    DINNER("Obiadek", R.drawable.ic_dinner, listOf(IngredientCategory.OILS, IngredientCategory.PORRIDGE, IngredientCategory.DINNERS, IngredientCategory.DIARY, IngredientCategory.FRUITS, IngredientCategory.FRUITS_TUBE)),
    OTHER("Przekąska", R.drawable.ic_dinner, listOf(IngredientCategory.OTHERS));

    companion object {
        fun fromString(s: String) = values().firstOrNull { it.string == s || it.name == s} ?: OTHER
    }
}

data class MealsGroup(
        val day: Int,
        val meals: List<Meal>
)

data class MealsViewModel(
        val mealsData: List<MealsGroup>,
        val groups: Int,
        val ranges: List<IntRange>
): List<MealsGroup> by mealsData {

    val viewsCount: Int = mealsData.size + mealsData.map { it.meals }.flatten().size
}

data class IngredientsGroups(
        val category: IngredientCategory,
        val ingredients: List<Ingredient>
)

data class IngredientsViewModel(
        val ingredients: List<IngredientsGroups>,
        val groups: Int,
        val ranges: List<IntRange>
): List<IngredientsGroups> by ingredients {

    val viewsCount: Int = ingredients.size + ingredients.map { it.ingredients }.flatten().size
}