package pl.kpob.dietdiary.mapper

import org.joda.time.DateTime
import pl.kpob.dietdiary.MealDetails
import pl.kpob.dietdiary.MealIngredient
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.repo.Mapper

/**
 * Created by kpob on 11.12.2017.
 */
class MealDetailsMapper: Mapper<MealDTO, MealDetails>, BaseMealMapper() {

    override fun map(input: MealDTO?): MealDetails? {
        if(input == null) return null
        val ingredientsWithWeight = input.toIngredientsWithWeight()
        val dt = DateTime(input.time)

        return MealDetails(
                time = input.time.toDateString(),
                date = "${dt.dayOfMonth}-${dt.monthOfYear}-${dt.year}",
                type = input.name.toMealType(),
                caloriesTotal = calculateCalories(ingredientsWithWeight),
                protein = amountOfNutrient(ingredientsWithWeight) { it.protein },
                carbohydrates = amountOfNutrient(ingredientsWithWeight) { it.carbohydrates },
                salt = amountOfNutrient(ingredientsWithWeight) { it.salt },
                lct = amountOfNutrient(ingredientsWithWeight) { it.lct },
                mtc = amountOfNutrient(ingredientsWithWeight) { it.mtc },
                roughage = amountOfNutrient(ingredientsWithWeight) { it.roughage },
                ingredients = ingredientsWithWeight.map { (i, w) -> MealIngredient(i.id, i.name, i.calories, w) }.filter { it.weight > 0 }
        )
    }

    override fun map(input: List<MealDTO>): List<MealDetails> = input.mapNotNull { map(it) }

}