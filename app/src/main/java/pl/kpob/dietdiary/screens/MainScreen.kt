package pl.kpob.dietdiary.screens

import android.content.Context
import android.support.v7.app.AlertDialog
import com.wealthfront.magellan.rx.RxScreen
import io.realm.Sort
import org.jetbrains.anko.AnkoLogger
import org.joda.time.DateTime
import pl.kpob.dietdiary.Meal
import pl.kpob.dietdiary.MealType
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.mapper.MealMapper
import pl.kpob.dietdiary.usingRealm
import pl.kpob.dietdiary.views.MainView
import pl.kpob.dietdiary.views.utils.TimePicker


/**
 * Created by kpob on 20.10.2017.
 */
class MainScreen : RxScreen<MainView>(), AnkoLogger {

    private val fbSaver by lazy { FirebaseSaver() }

    private val meals get() = usingRealm {
        MealMapper.map(it.where(MealDTO::class.java).findAllSorted("time", Sort.DESCENDING))
    }

    override fun createView(context: Context?) = MainView(context!!)

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        view?.let {
            it.toolbarTitle = "Dziennik Stasia"
            it.initMenu(R.menu.menu_main) {
                when (it) {
                    R.id.action_new_ingredient -> view.post { navigator.goTo(AddIngredientScreen()) }
                    R.id.action_all_ingredients -> view.post { navigator.goTo(IngredientsListScreen()) }
                }
            }
            it.showMeals(meals)
        }
    }

    fun onDessertClick() = goToNewMealScreen(MealType.DESSERT)

    fun onDinnerClick() = goToNewMealScreen(MealType.DINNER)

    fun onMilkClick() = goToNewMealScreen(MealType.MILK)

    fun onItemClick(item: Meal) = navigator.goTo(PieChartScreen(item.id))

    fun onTimeClick(item: Meal) = showDialog {
        TimePicker().dialog(activity) { m, h ->
            val newTimestamp = DateTime(item.timestamp)
                    .withMinuteOfHour(m)
                    .withHourOfDay(h)
                    .millis
            item.updateTimestamp(newTimestamp)
        }
    }


    fun updateData() {
        view.postDelayed({ view?.showMeals(meals) }, 1000)
    }

    fun onItemLongClick(item: Meal): Boolean = true

    fun onDeleteClick(item: Meal) = showDialog {
        AlertDialog.Builder(activity)
                .setMessage("Czy na pewno chcesz usunąć posiłek?")
                .setNegativeButton("Anuluj") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Usuń") { _, _ -> fbSaver.removeMeal(item) }
                .create()
    }

    fun onEditClick(item: Meal) = navigator.goTo(AddMealScreen(item.type, item))

    fun onAddIngredientClick() = navigator.goTo(AddIngredientScreen())

    private fun goToNewMealScreen(type: MealType) = navigator.goTo(AddMealScreen(type))

    private fun Meal.updateTimestamp(newValue: Long) {
        fbSaver.updateMealTime(id, newValue)
    }


}