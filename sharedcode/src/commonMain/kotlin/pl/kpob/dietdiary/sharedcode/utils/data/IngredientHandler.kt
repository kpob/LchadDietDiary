package pl.kpob.dietdiary.sharedcode.utils

import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.IngredientDTO
import pl.kpob.dietdiary.sharedcode.repository.Repository

interface IngredientHandler {

    fun save(repo: Repository<IngredientDTO, Ingredient>, ingredientDTO: FbIngredient)
    fun delete(repo: Repository<IngredientDTO, Ingredient>, ingredient: Ingredient)
    fun asFbModel(ingredient: Ingredient): FbIngredient
}