package pl.kpob.dietdiary.sharedcode.repository.mapper

import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.Ingredient

class FbIngredientMapper: Mapper<FbIngredient, Ingredient> {

    override fun map(input: FbIngredient?): Ingredient? {
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

    override fun map(input: List<FbIngredient>): List<Ingredient> = input.map { input ->
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