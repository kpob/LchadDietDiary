package pl.kpob.dietdiary.sharedcode.view

import pl.kpob.dietdiary.sharedcode.viewmodel.IngredientsViewModel

interface IngredientListView {

    fun initList(ingredients: IngredientsViewModel)
    fun displayMessage(message: String)
    fun updateList(ingredients: IngredientsViewModel)

    var viewTitle: String
}