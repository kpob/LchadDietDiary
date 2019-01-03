package pl.kpob.dietdiary.sharedcode.view

import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.MealIngredient

interface AddMealView {

    fun displayError(message: String)
    fun setExistingData(ingredients: List<MealIngredient>, possibleIngredients: List<Ingredient>)
    fun addInitialRow(possibleIngredients: List<Ingredient>)
    fun addRow(possibleIngredients: List<Ingredient>)
    fun addRows(ingredients: List<Ingredient>, possibleIngredients: List<Ingredient>)

    var totalWeight: Float
    var time: String
    var viewTitle: String
}