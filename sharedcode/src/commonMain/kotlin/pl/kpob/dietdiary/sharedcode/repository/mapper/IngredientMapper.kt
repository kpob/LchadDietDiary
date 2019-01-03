package pl.kpob.dietdiary.sharedcode.repository.mapper

import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.IngredientDTO

class IngredientMapper: Mapper<IngredientDTO, Ingredient> {

    override fun map(input: IngredientDTO?): Ingredient? {
        if(input == null) return null
        return Ingredient(
                input.id,
                input.name,
                input.mtc,
                input.lct,
                input.carbohydrates,
                input.protein,
                input.salt,
                input.roughage,
                input.calories,
                input.category,
                input.useCount
        )
    }

    override fun map(input: List<IngredientDTO>): List<Ingredient> = input.map { input ->
         Ingredient(
                input.id,
                input.name,
                input.mtc,
                input.lct,
                input.carbohydrates,
                input.protein,
                input.salt,
                input.roughage,
                input.calories,
                input.category,
                input.useCount
        )
    }
}