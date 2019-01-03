package pl.kpob.dietdiary.sharedcode.view

import pl.kpob.dietdiary.sharedcode.model.FbIngredient

interface AddIngredientView {
    fun preFill(ingredient: FbIngredient, category: Int)

    var viewTitle: String
}