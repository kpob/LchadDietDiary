package pl.kpob.dietdiary.sharedcode.repository.mapper.mocks

import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.repository.mapper.Mapper
import pl.kpob.dietdiary.sharedcode.utils.MockDataProvider

object FakeMealDetailsMapper: Mapper<MealDTO, MealDetails> {
    override fun map(input: MealDTO?): MealDetails? {
        val meal = MockDataProvider.provideMeals().firstOrNull()
        val i = MockDataProvider.provideIngredients()

        return MealDetails(
                time = "12:00",
                date = "12-12-2018",
                type = MealType.DESSERT,
                caloriesTotal = 252.3f,
                carbohydrates = 100f,
                dayOfYear = 333,
                lct = 4f,
                mtc = 0.5f,
                protein = 10f,
                salt = 1f,
                roughage = 5f,
                year = 2018,
                ingredients = i.map {
                    MealIngredient(it.id, it.name, 31f, 42f)
                }


        )
    }

    override fun map(input: List<MealDTO>): List<MealDetails>  {
        val i = MockDataProvider.provideIngredients()

        return listOf(MealDetails(
                time = "12:00",
                date = "12-12-2018",
                type = MealType.DESSERT,
                caloriesTotal = 252.3f,
                carbohydrates = 100f,
                dayOfYear = 333,
                lct = 4f,
                mtc = 0.5f,
                protein = 10f,
                salt = 1f,
                roughage = 5f,
                year = 2018,
                ingredients = i.map {
                    MealIngredient(it.id, it.name, 31f, 42f)
                }
        ))

    }
}