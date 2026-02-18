package pl.kpob.dietdiary.mapper

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import pl.kpob.dietdiary.App
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.db.MealWithIngredients

/**
 * Created by kpob on 11.12.2017.
 */
abstract class BaseMealMapper(
        private val allIngredients: List<IngredientDTO> = App.db.ingredientDao().getAll()
) {

    protected fun amountOfNutrient(mealIngredients: List<Pair<IngredientDTO, Float>>, nutrient: (IngredientDTO) -> Float): Float =
            mealIngredients.map { (i, w) -> nutrient(i) * w * 0.01f }.sum()

    protected fun calculateCalories(mealIngredients: List<Pair<IngredientDTO, Float>>): Float =
            mealIngredients.map { (i, weight) -> i.calories * weight / 100f }.sum()

    protected fun calculateLct(mealIngredients: List<Pair<IngredientDTO, Float>>): Float =
            mealIngredients.map { (i, weight) -> i.lct * weight / 100f }.sum()

    protected fun Long.toDateString() =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault()).let {
                val (h, m) = it.hour to it.minute
                "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}"
            }

    protected fun String.toMealType() = MealType.fromString(this)

    protected fun MealWithIngredients.toIngredientsWithWeight(): List<Pair<IngredientDTO, Float>> {
        return ingredients.mapNotNull { mealIngredient ->
            val dto = allIngredients.firstOrNull { it.id == mealIngredient.ingredientId }
            dto?.let { it to mealIngredient.weight }
        }
    }
}
