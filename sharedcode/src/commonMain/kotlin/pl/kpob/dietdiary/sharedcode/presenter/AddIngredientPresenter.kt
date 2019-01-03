package pl.kpob.dietdiary.sharedcode.presenter

import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.IngredientCategory
import pl.kpob.dietdiary.sharedcode.model.IngredientDTO
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.utils.IngredientHandler
import pl.kpob.dietdiary.sharedcode.utils.nextId
import pl.kpob.dietdiary.sharedcode.view.AddIngredientView
import pl.kpob.dietdiary.sharedcode.view.AppNavigator

class AddIngredientPresenter(
        private val ingredient: FbIngredient?,
        private val remoteDatabase: RemoteDatabase,
        private val repository: Repository<IngredientDTO, Ingredient>,
        private val appNavigator: AppNavigator,
        private val ingredientHandler: IngredientHandler
) {

    private var view: AddIngredientView? = null

    fun onShow(view: AddIngredientView) {
        this.view = view
        view.viewTitle = "Dodaj składnik"

        if (ingredient == null) return
        val category = IngredientCategory.stringValues().indexOfFirst { it == IngredientCategory.fromInt(ingredient.category).label  }
        view.preFill(ingredient, category)
    }

    fun onSaveClick(name: String, kcal: Float, lct: Float, mct: Float, carbohydrates: Float, protein: Float, roughage: Float, salt: Float, type: IngredientCategory) {
        val update = ingredient != null
        val newIngredient = if(update) {
            FbIngredient(ingredient!!.id, name, mct, lct, carbohydrates, protein, salt, roughage, kcal, type.value, ingredient.useCount, false)
        } else {
            FbIngredient(nextId(), name, mct, lct, carbohydrates, protein, salt, roughage, kcal, type.value, 0,  false)
        }
        remoteDatabase.saveIngredient(newIngredient, update)
        ingredientHandler.save(repository, newIngredient)
        appNavigator.goBack()
    }
}