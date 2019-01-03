package pl.kpob.dietdiary.sharedcode.repository.mapper

import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.utils.MyDateTime

class MealDetailsMapper(ingredients: List<Ingredient>): Mapper<MealDTO, MealDetails>, BaseMealMapper(ingredients) {

    override fun map(input: MealDTO?): MealDetails? {
        if(input == null) return null
        val ingredientsWithWeight = input.toIngredientsWithWeight()
        val dt = MyDateTime(input.time)

        return MealDetails(
                time = input.time.toDateString(),
                date = dt.date,
                type = input.name.toMealType(),
                caloriesTotal = calculateCalories(ingredientsWithWeight),
                protein = amountOfNutrient(ingredientsWithWeight) { it.protein },
                carbohydrates = amountOfNutrient(ingredientsWithWeight) { it.carbohydrates },
                salt = amountOfNutrient(ingredientsWithWeight) { it.salt },
                lct = amountOfNutrient(ingredientsWithWeight) { it.lct },
                mtc = amountOfNutrient(ingredientsWithWeight) { it.mtc },
                roughage = amountOfNutrient(ingredientsWithWeight) { it.roughage },
                ingredients = ingredientsWithWeight.map { (i, w) -> MealIngredient(i.id, i.name, i.calories, w) }.filter { it.weight > 0 },
                dayOfYear = dt.dayOfYear,
                year = dt.year
        )
    }

    override fun map(input: List<MealDTO>): List<MealDetails> = input.mapNotNull { map(it) }

}