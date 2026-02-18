package pl.kpob.dietdiary.screens

import android.content.Context
import com.wealthfront.magellan.rx.RxScreen
import pl.kpob.dietdiary.AnkoLogger
import pl.kpob.dietdiary.asReadableString
import pl.kpob.dietdiary.currentTime
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.firebase.FbMeal
import pl.kpob.dietdiary.firebase.FbMealIngredient
import pl.kpob.dietdiary.nextId
import pl.kpob.dietdiary.screens.utils.MealDataInteractor
import pl.kpob.dietdiary.screens.utils.TimePickerCreator
import pl.kpob.dietdiary.screens.utils.Traits
import pl.kpob.dietdiary.views.AddMealView


/**
 * Created by kpob on 20.10.2017.
 */
class AddMealScreen(private val type: MealType, private val meal: Meal? = null) :
        RxScreen<AddMealView>(),
        MealDataInteractor by Traits.mealInteractor(type, meal),
        TimePickerCreator by Traits.timePickerCreator(),
        AnkoLogger {

    private var mealTime: Long = meal?.timestamp ?: currentTime()
    private var factor: Float = 1f

    override fun createView(context: Context?) = AddMealView(context!!)

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        setupView()
    }

    fun onAddClick(data: List<Pair<Ingredient, Float>>) {
        info { "data: ${data.map { it.first.name to it.second }}" }
        if (data.isEmpty() || data.all { it.second == .0f }) {
            toast("Nie można zapisać pustego posiłku")
            return
        }

        val meal = when {
            this.meal != null -> FbMeal(meal.id, mealTime, type.name, data.asFbMealIngredients())
            else -> FbMeal(nextId(), mealTime, type.name, data.asFbMealIngredients())
        }
        val useCounters = data.map { it.first }.map { Pair(it.id, it.useCount + 1) }

        saveMeal(meal, useCounters) {
            navigator.handleBack()
        }
    }

    fun onTimeEditClick() = showDialog {
        createTimePicker(activity) {
            mealTime = it
            view.time = mealTime.asReadableString
        }
    }

    fun onProgressChanged(progress: Int) {
        factor = progress.toFloat() / 100f
        view?.progress = "$progress%"
    }

    private fun setupView() = view?.let {
        it.enableHomeAsUp { navigator.handleBack() }
        it.time = mealTime.asReadableString
        it.progress = "100%"

        when (meal) {
            null -> {
                it.addInitialRow()
                it.toolbarTitle = "Nowy posiłek - ${type.string}"
            }
            else -> {
                it.setExistingData(ingredients, possibleIngredients)
                it.toolbarTitle = "Edytuj posiłek - ${type.string}"
            }
        }
    }

    private fun List<Pair<Ingredient, Float>>.asFbMealIngredients() = map {
        FbMealIngredient(it.first.id, it.second * factor)
    }

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    companion object {
        fun Ingredient.toString(): String = name
    }
}