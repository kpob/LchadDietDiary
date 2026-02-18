package pl.kpob.dietdiary.screens

import android.content.Context
import android.os.Handler
import com.wealthfront.magellan.rx.RxScreen
import pl.kpob.dietdiary.AnkoLogger
import pl.kpob.dietdiary.dbAsync
import pl.kpob.dietdiary.toast
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebaseDb
import pl.kpob.dietdiary.ingredientsRef
import pl.kpob.dietdiary.repo.IngredientRepository
import pl.kpob.dietdiary.repo.MealRepository
import pl.kpob.dietdiary.views.IngredientsListView

/**
 * Created by kpob on 22.10.2017.
 */
class IngredientsListScreen: RxScreen<IngredientsListView>(), AnkoLogger {

    private val ingredientRepository by lazy { IngredientRepository() }
    private val mealRepository by lazy { MealRepository() }
    private val data: List<Ingredient> get() = ingredientRepository.getAll()

    private val handler = Handler()

    override fun createView(context: Context?) = IngredientsListView(context!!)

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        view?.let {
            it.toolbarTitle = "Składniki"
            it.enableHomeAsUp { navigator.goBack() }
            it.initList(data)
        }
    }

    fun onDeleteClick(item: Ingredient) {
        val count = mealRepository.getMealsContainingIngredient(item.id).size
        when (count) {
            0 -> deleteIngredient(item)
            else -> toast("Nie można usunąć - występuje w $count posiłkach")
        }
    }

    private fun deleteIngredient(item: Ingredient) {
        val toUpdate = mapOf<String, Any>(item.id to item.toFirebase(true))
        firebaseDb.ingredientsRef.updateChildren(toUpdate)

        dbAsync(
            block = { ingredientRepository.deleteById(item.id) },
            callback = { handler.post { view.updateList(data) } }
        )
    }

    fun onEditClick(item: Ingredient) {
        navigator.goTo(AddIngredientScreen(item.toFirebase()))
    }

    fun Ingredient.toFirebase(deleted: Boolean = false): FbIngredient = FbIngredient(
        id, name, mtc, lct, carbohydrates, protein, salt, roughage, calories, category, useCount, deleted
    )
}
