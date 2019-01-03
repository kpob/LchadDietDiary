package pl.kpob.dietdiary.sharedcode.viewmodel

import pl.kpob.dietdiary.sharedcode.model.Meal

object MealDataMapper {

    fun mealsAsViewModel(meals: List<Meal>): MealsViewModel {
        val groups = meals.groupBy { it.dayOfYear }.toList().map { MealsGroup(it.first, it.second) }
        val ranges = (0 until groups.size).map {
            val start = (1 + it + groups.take(it).map { it.meals }.flatten().count())
            val end = (groups.take(it + 1).map { it.meals }.flatten().count() + it)
            start..end
        }
        return MealsViewModel(groups, groups.size, ranges)
    }
}