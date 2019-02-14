package pl.kpob.dietdiary.sharedcode.repository.mapper.mocks

import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.IngredientDTO
import pl.kpob.dietdiary.sharedcode.repository.mapper.Mapper
import pl.kpob.dietdiary.sharedcode.utils.MockDataProvider

object FakeIngredientMapper: Mapper<IngredientDTO, Ingredient> {

    override fun map(input: IngredientDTO?): Ingredient? {
        return MockDataProvider.provideIngredients().firstOrNull()
    }

    override fun map(input: List<IngredientDTO>): List<Ingredient> {
        return MockDataProvider.provideIngredients()
    }
}