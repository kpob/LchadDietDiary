package pl.kpob.dietdiary.mapper

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import pl.kpob.dietdiary.AnkoLogger
import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.db.MealWithIngredients
import pl.kpob.dietdiary.repo.Mapper

/**
 * Created by kpob on 22.10.2017.
 */
object MealMapper : Mapper<MealWithIngredients, Meal>, BaseMealMapper(), AnkoLogger {

    private val currentDay by lazy { ZonedDateTime.now().dayOfYear }

    override fun map(input: MealWithIngredients?): Meal? {
        if (input == null) return null

        val dt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(input.meal.time), ZoneId.systemDefault())
        val (h, m) = dt.hour to dt.minute
        val time = "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}"

        val ingredientsWithWeight = input.toIngredientsWithWeight()
        return Meal(
                input.meal.id,
                time,
                "${dt.dayOfMonth}-${dt.monthValue}-${dt.year}",
                input.meal.name.toMealType(),
                calculateCalories(ingredientsWithWeight),
                calculateLct(ingredientsWithWeight),
                dt.dayOfYear,
                dt.year,
                input.meal.time,
                currentDay == dt.dayOfYear
        )
    }

    override fun map(input: List<MealWithIngredients>): List<Meal> = input.mapNotNull { map(it) }

}
