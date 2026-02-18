package pl.kpob.dietdiary.screens

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.wealthfront.magellan.rx.RxScreen
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import pl.kpob.dietdiary.AnkoLogger
import pl.kpob.dietdiary.MainActivity
import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.repo.AllMealsSortedSpecification
import pl.kpob.dietdiary.repo.MealRepository
import pl.kpob.dietdiary.views.MainView
import pl.kpob.dietdiary.views.utils.TimePicker


/**
 * Created by kpob on 20.10.2017.
 */
class MainScreen() : RxScreen<MainView>(), AnkoLogger {

    private val repo by lazy { MealRepository() }

    private val fbSaver by lazy { FirebaseSaver() }

    private val meals get() = repo.withRealmQuery { AllMealsSortedSpecification(it) }

    override fun createView(context: Context?) = MainView(context!!)

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        val intent = activity.intent
        if(intent.hasExtra(MainActivity.EXTRA_MEAL)) {
            val screen = AddMealScreen(MealType.fromString(intent.getStringExtra(MainActivity.EXTRA_MEAL)))
            intent.removeExtra(MainActivity.EXTRA_MEAL)
            navigator.goTo(screen)
            return
        }

        view?.let {
            it.toolbarTitle = context?.getString(R.string.app_name) ?: ""
            it.initMenu(R.menu.menu_main) {
                when (it) {
                    R.id.action_new_ingredient -> view.post { navigator.goTo(AddIngredientScreen()) }
                    R.id.action_all_ingredients -> view.post { navigator.goTo(IngredientsListScreen()) }
                    R.id.action_tags -> view.post { navigator.goTo(TagCloudScreen()) }
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
            val newTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(item.timestamp), ZoneId.systemDefault())
                    .withMinute(m)
                    .withHour(h)
                    .toInstant().toEpochMilli()
            item.updateTimestamp(newTimestamp)
        }
    }

    fun updateData() = view.postDelayed({ view?.showMeals(meals) }, 1000)

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

    fun onLabelClick(meals: List<Meal>) {
        navigator.goTo(PieChartScreen(meals.map { it.id }))
    }


}