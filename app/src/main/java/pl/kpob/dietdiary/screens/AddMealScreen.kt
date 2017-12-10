package pl.kpob.dietdiary.screens

import android.content.Context
import com.wealthfront.magellan.rx.RxScreen
import io.realm.Sort
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.db.DataSaver
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.server.FbMeal
import pl.kpob.dietdiary.server.FbMealIngredient
import pl.kpob.dietdiary.views.AddMealView

/**
 * Created by kpob on 20.10.2017.
 */
class AddMealScreen(private val type: MealType, private val meal: Meal? = null) : RxScreen<AddMealView>(), AnkoLogger {

    private val fbSaver by lazy { FirebaseSaver() }

    val data: List<IngredientDTO> by lazy {
        val categories = type.filters.map { it.value }.toTypedArray()
        usingRealm {
            val result = it.where(IngredientDTO::class.java)
                    .`in`("category", categories)
                    .findAllSorted(arrayOf("useCount", "name"), arrayOf(Sort.DESCENDING, Sort.ASCENDING))
            it.copyFromRealm(result)
        }
    }

    private val ingredients by lazy {
        usingRealm {
            it.copyFromRealm(it.where(MealDTO::class.java).equalTo("id", meal!!.id).findAll().map {
                it.ingredients
            }.flatten())
        }
    }

    override fun createView(context: Context?): AddMealView {
        return AddMealView(context!!)
    }

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        view?.let {
            it.enableHomeAsUp { navigator.goBack() }

            if(meal != null) {
                it.setExistingData(ingredients, data)
                it.toolbarTitle = "Edytuj posiłek"
            } else {
                it.addInitialRow()
                it.toolbarTitle = "Nowy posiłek"
            }
        }
    }

    fun onAddClick(data: List<Pair<IngredientDTO, Float>>) {
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

        DataSaver.incrementIngredientsUseCount(meal.ingredients)
        DataSaver.saveMeal(meal) { success ->
            if(success) {
                navigator.goBack()
            } else {
                view?.context?.toast("Błąd")
            }
        }
    }

}