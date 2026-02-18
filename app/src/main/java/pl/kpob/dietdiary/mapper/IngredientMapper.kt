package pl.kpob.dietdiary.mapper

import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.repo.Mapper

class IngredientMapper : Mapper<IngredientDTO, Ingredient> {

    override fun map(input: IngredientDTO?): Ingredient? {
        if (input == null) return null
        return Ingredient(
                id = input.id,
                name = input.name,
                mtc = input.mtc,
                lct = input.lct,
                carbohydrates = input.carbohydrates,
                protein = input.protein,
                salt = input.salt,
                roughage = input.roughage,
                calories = input.calories,
                category = input.category,
                useCount = input.useCount
        )
    }

    override fun map(input: List<IngredientDTO>): List<Ingredient> = input.mapNotNull { map(it) }
}
