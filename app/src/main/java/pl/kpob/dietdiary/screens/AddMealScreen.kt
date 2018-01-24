package pl.kpob.dietdiary.screens

import android.content.Context
import com.wealthfront.magellan.rx.RxScreen
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.firebase.FbMealIngredient
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.repo.*
import pl.kpob.dietdiary.firebase.FbMeal
import pl.kpob.dietdiary.views.AddMealView
import pl.kpob.dietdiary.views.utils.TimePicker

/**
 * Created by kpob on 20.10.2017.
 */
class AddMealScreen(private val type: MealType, private val meal: Meal? = null) : RxScreen<AddMealView>(), AnkoLogger {

    private val fbSaver by lazy { FirebaseSaver() }

    private val ingredientRepo by lazy { IngredientRepository() }
    private val mealRepo by lazy { MealDetailsRepository() }

    private var mealTime: Long = meal?.timestamp ?: currentTime()

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
            it.time = mealTime.asReadableString

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
            this.meal != null -> FbMeal(meal.id, mealTime, type.name, data.map { FbMealIngredient(it.first.id, it.second) })
            else -> FbMeal(nextId(), mealTime, type.name, data.map { FbMealIngredient(it.first.id, it.second) })
        }

        fbSaver.saveMeal(meal, this.meal != null)
        data.map { it.first }.map { Pair(it.id, it.useCount + 1) }.forEach {
            fbSaver.updateUsageCounter(it.first, it.second)
        }


        realmAsyncTransaction(
            transaction = { mealRepo.insert(meal.toRealm(), RealmAddTransaction(it)) },
            callback = { navigator.goBack() }
        )
    }

    fun onTimeEditClick() = showDialog {
        TimePicker().dialog(activity) { m, h ->
            val newTimestamp = DateTime(currentTime())
                    .withMinuteOfHour(m)
                    .withHourOfDay(h)
                    .millis

            mealTime = newTimestamp
            view.time = mealTime.asReadableString
        }

    }

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    companion object {
        fun Ingredient.toString(): String {
            return name
        }
    }

}