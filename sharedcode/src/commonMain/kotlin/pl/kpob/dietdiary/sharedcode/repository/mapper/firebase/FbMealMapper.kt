package pl.kpob.dietdiary.sharedcode.repository.mapper.firebase

import pl.kpob.dietdiary.sharedcode.model.FbMeal
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.repository.mapper.BaseMealMapper
import pl.kpob.dietdiary.sharedcode.repository.mapper.Mapper
import pl.kpob.dietdiary.sharedcode.utils.MyDateTime

class FbMealMapper(ingredients: List<Ingredient>): Mapper<FbMeal, Meal>, BaseMealMapper(ingredients) {

    private val currentDay by lazy { MyDateTime().dayOfYear }

    override fun map(input: FbMeal?): Meal? {
        if(input == null) return null

        val dt = MyDateTime(input.time)
        val (h, m) = dt.hourOfDay to dt.minuteOfHour
        val time = "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}"

        val ingredientsWithWeight = input.toIngredientsWithWeight()
        return Meal(
                input.id,
                time,
                dt.date,
                input.name.toMealType(),
                calculateCalories(ingredientsWithWeight),
                calculateLct(ingredientsWithWeight),
                dt.dayOfYear,
                dt.year,
                input.time,
                currentDay == dt.dayOfYear
        )
    }

    override fun map(input: List<FbMeal>): List<Meal> = input.mapNotNull { map(it) }

}