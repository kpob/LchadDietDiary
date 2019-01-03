package pl.kpob.dietdiary.sharedcode.repository.mapper

import pl.kpob.dietdiary.sharedcode.model.FbMeal
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.model.MealType
import pl.kpob.dietdiary.sharedcode.utils.MyDateTime

abstract class BaseMealMapper(private val allIngredients: List<Ingredient>) {

    protected fun amountOfNutrient(mealIngredients: List<Pair<Ingredient, Float>>, nutrient: (Ingredient) -> Float) : Float =
            mealIngredients.map { (i, w) -> nutrient(i) * w * 0.01f}.sum()

    protected fun calculateCalories(mealIngredients: List<Pair<Ingredient, Float>>): Float =
            mealIngredients.map { (i, weight) -> i.calories * weight / 100f }.sum()

    protected fun calculateLct(mealIngredients: List<Pair<Ingredient, Float>>): Float =
            mealIngredients.map { (i, weight) -> i.lct * weight / 100f }.sum()

    protected fun Long.toDateString() =
            MyDateTime(this).let {
                val (h, m) = it.hourOfDay to it.minuteOfHour
                "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}"
            }

    protected fun String.toMealType() = MealType.fromString(this)

    protected fun MealDTO.toIngredientsWithWeight(): List<Pair<Ingredient, Float>> {
        val mealIngredientsIds = ingredients.map { it.ingredientId }
        return allIngredients
                .filter { mealIngredientsIds.contains(it.id) }
                .map { i -> i to ingredients.first { it.ingredientId == i.id }.weight }
    }

    protected fun FbMeal.toIngredientsWithWeight(): List<Pair<Ingredient, Float>> {
        val mealIngredientsIds = ingredients.map { it.ingredientId }
        return allIngredients
                .filter { mealIngredientsIds.contains(it.id) }
                .map { i -> i to ingredients.first { it.ingredientId == i.id }.weight }
    }

}