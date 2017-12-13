package pl.kpob.dietdiary.mapper

import pl.kpob.dietdiary.Ingredient
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.repo.Mapper

/**
 * Created by kpob on 11.12.2017.
 */
class IngredientMapper: Mapper<IngredientDTO, Ingredient> {
    override fun map(input: IngredientDTO?): Ingredient? {
        if(input == null) return null
        return Ingredient(
                input.id,
                input.name,
                input.calories,
                input.lct,
                input.mtc,
                input.protein,
                input.salt,
                input.roughage,
                input.calories,
                input.category,
                input.useCount
        )
    }

    override fun map(input: List<IngredientDTO>): List<Ingredient> = input.mapNotNull { map(it) }
}