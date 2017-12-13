package pl.kpob.dietdiary.screens

import android.content.Context
import com.wealthfront.magellan.rx.RxScreen
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.repo.*
import pl.kpob.dietdiary.server.FbMeal
import pl.kpob.dietdiary.server.FbMealIngredient
import pl.kpob.dietdiary.views.AddMealView

/**
 * Created by kpob on 20.10.2017.
 */
class AddMealScreen(private val type: MealType, private val meal: Meal? = null) : RxScreen<AddMealView>(), AnkoLogger {

    private val fbSaver by lazy { FirebaseSaver() }

    private val ingredientRepo by lazy { IngredientRepository() }
    private val mealRepo by lazy { MealDetailsRepository() }

    val possibleIngredients: List<Ingredient> by lazy {
        ingredientRepo.withRealmQuery { IngredientsByMealTypeSpecification(it, type) }
    }

    private val ingredients by lazy {
        mealRepo.withRealmSingleQuery { MealByIdSpecification(it, meal!!.id) }?.ingredients ?: listOf()
    }

    override fun createView(context: Context?): AddMealView {
        return AddMealView(context!!)
    }

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        view?.let {
            it.enableHomeAsUp { navigator.goBack() }

            if(meal != null) {
                it.setExistingData(ingredients, possibleIngredients)
                it.toolbarTitle = "Edytuj posiłek"
            } else {
                it.addInitialRow()
                it.toolbarTitle = "Nowy posiłek"
            }
        }
    }

    fun onAddClick(data: List<Pair<Ingredient, Float>>) {
        if (data.all { it.second == .0f }) {
            view?.context?.toast("Nie można zapisać pustego posiłku")
            return
        }

        val meal = when {
            this.meal != null -> FbMeal(meal.id, meal.timestamp, type.name, data.map { FbMealIngredient(it.first.id, it.second) })
            else -> FbMeal(nextId(), currentTime(), type.name, data.map { FbMealIngredient(it.first.id, it.second) })
        }

        fbSaver.saveMeal(meal, this.meal != null)
        data.map { it.first }.map { Pair(it.id, it.useCount + 1) }.forEach {
            fbSaver.updateUsageCounter(it.first, it.second)
        }


        realmAsyncTransaction(
            f = { mealRepo.insert(meal.toRealm(), RealmAddTransaction(it)) },
            cb = { navigator.goBack() }
        )
    }

}