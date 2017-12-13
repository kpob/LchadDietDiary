package pl.kpob.dietdiary.mapper

import org.jetbrains.anko.AnkoLogger
import org.joda.time.DateTime
import pl.kpob.dietdiary.Meal
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.repo.Mapper

/**
 * Created by kpob on 22.10.2017.
 */
object MealMapper : Mapper<MealDTO, Meal>, BaseMealMapper(), AnkoLogger{

    private val currentDay by lazy { DateTime().dayOfYear }

    override fun map(input: MealDTO?): Meal? {
        if(input == null) return null

        val dt = DateTime(input.time)
        val (h, m) = dt.hourOfDay to dt.minuteOfHour
        val time = "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}"

        val ingredientsWithWeight = input.toIngredientsWithWeight()
        return Meal(
                input.id,
                time,
                "${dt.dayOfMonth}-${dt.monthOfYear}-${dt.year}",
                input.name.toMealType(),
                calculateCalories(ingredientsWithWeight),
                calculateLct(ingredientsWithWeight),
                dt.dayOfYear,
                dt.year,
                input.time,
                currentDay == dt.dayOfYear
        )
    }

    override fun map(input: List<MealDTO>): List<Meal> = input.mapNotNull { map(it) }

}