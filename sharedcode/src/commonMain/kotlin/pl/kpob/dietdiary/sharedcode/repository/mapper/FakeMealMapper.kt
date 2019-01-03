package pl.kpob.dietdiary.sharedcode.repository.mapper

import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.utils.MockDataProvider

object FakeMealMapper: Mapper<MealDTO, Meal> {

    override fun map(input: MealDTO?): Meal? {
        return MockDataProvider.provideMeals().firstOrNull()
    }

    override fun map(input: List<MealDTO>): List<Meal> {
        return MockDataProvider.provideMeals()
    }
}