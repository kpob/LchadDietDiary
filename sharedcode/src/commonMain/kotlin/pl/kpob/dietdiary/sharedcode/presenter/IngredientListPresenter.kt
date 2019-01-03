package pl.kpob.dietdiary.sharedcode.presenter

import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.repository.*
import pl.kpob.dietdiary.sharedcode.utils.IngredientHandler
import pl.kpob.dietdiary.sharedcode.view.AppNavigator
import pl.kpob.dietdiary.sharedcode.view.IngredientListView
import pl.kpob.dietdiary.sharedcode.viewmodel.IngredientsGroups
import pl.kpob.dietdiary.sharedcode.viewmodel.IngredientsViewModel

class IngredientListPresenter(
        private val appNavigator: AppNavigator,
        private val ingredientRepository: Repository<IngredientDTO, Ingredient>,
        private val mealRepository: Repository<MealDTO, Meal>,
        private val remoteDatabase: RemoteDatabase,
        private val ingredientHandler: IngredientHandler) {

    private var view: IngredientListView? = null

    fun onShow(view: IngredientListView) {
        view.viewTitle = "Składniki"
        val ingredients = queryData()
        view.initList(ingredients)
    }

    fun onDeleteClick(item: Ingredient) {
        val spec = MealsWithIngredientSpecification(item.id)
        val count = mealRepository.query(spec).size
        when(count) {
            0 -> {
                deleteIngredient(item)
                val ingredients = queryData()
                view?.updateList(ingredients)
            }
            else -> view?.displayMessage("Nie można usunąć - występuje w $count posiłkach")
        }
    }

    fun onEditClick(item: Ingredient) {
        appNavigator.goToAddIngredientView(/*item.toFirebase()*/)
    }

    private fun deleteIngredient(item: Ingredient)  {
        remoteDatabase.deleteIngredient(item)
        ingredientHandler.delete(ingredientRepository, item)
    }

    private fun queryData(): IngredientsViewModel {
        val ingredients = ingredientRepository.query(AllIngredientsSpecification())
        val groups = ingredients.groupBy { it.category }.toList()
        val groupsCount = groups.size
        val ranges = (0 until groupsCount).map {
            (1 + it + groups.take(it).map { it.second }.flatten().count())..(groups.take(it + 1).map { it.second }.flatten().count() + it)
        }
        return IngredientsViewModel(
                groups.map { IngredientsGroups(IngredientCategory.fromInt(it.first), it.second) },
                groupsCount,
                ranges
        )
    }
}