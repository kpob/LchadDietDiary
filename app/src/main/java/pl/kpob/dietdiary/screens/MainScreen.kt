package pl.kpob.dietdiary.screens

import android.content.Context
import android.support.v7.app.AlertDialog
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startService
import org.joda.time.DateTime
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.domain.MealsGroup
import pl.kpob.dietdiary.domain.MealsViewModel
import pl.kpob.dietdiary.events.MealsUpdatedEvent
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.repo.AllMealsSortedSpecification
import pl.kpob.dietdiary.repo.MealRepository
import pl.kpob.dietdiary.views.MainView
import pl.kpob.dietdiary.views.utils.TimePicker
import pl.kpob.dietdiary.worker.RefreshIngredientsDataService

/**
 * Created by kpob on 20.10.2017.
 */
class MainScreen : ScopedScreen<MainView>(), AnkoLogger {

    private val repo by lazy { MealRepository() }
    private val fbSaver by lazy { FirebaseSaver() }

    private fun queryMeals(): MealsViewModel {
        info { "[${Thread.currentThread()}] queryMeals()" }
        val meals = repo.withRealmQuery { AllMealsSortedSpecification(it) }
        val groups = meals.groupBy { it.dayOfYear }.toList().map { MealsGroup(it.first, it.second) }
        val ranges = (0 until groups.size).map {
            val start = (1 + it + groups.take(it).map { it.meals }.flatten().count())
            val end = (groups.take(it + 1).map { it.meals }.flatten().count() + it)
            start..end
        }
        return MealsViewModel(groups, groups.size, ranges)
    }

    override fun createView(context: Context?) = MainView(context!!).also {
        it.toolbarTitle = context.getString(R.string.app_name) ?: ""
        it.initMenu(R.menu.menu_main) {
            when (it) {
                R.id.action_new_ingredient -> view.post { navigator.goTo(AddIngredientScreen()) }
                R.id.action_all_ingredients -> view.post { navigator.goTo(IngredientsListScreen()) }
            }
        }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)
        EventBus.getDefault().register(this)
        launch {
            val intent = activity.intent
            if (intent.hasExtra(MainActivity.EXTRA_MEAL)) {
                val mealType = MealType.fromString(intent.getStringExtra(MainActivity.EXTRA_MEAL))
                intent.removeExtra(MainActivity.EXTRA_MEAL)
                withContext(uiContext) { navigator.goTo(AddMealScreen(mealType)) }
                return@launch
            }
            val meals = queryMeals()
            withContext(uiContext) {
                delay(300)
                view?.showMeals(meals)
            }
        }
    }

    override fun onHide(context: Context?) {
        EventBus.getDefault().unregister(this)
        super.onHide(context)
    }

    override fun onResume(context: Context?) {
        super.onResume(context)
        if (App.isSyncing) view?.showSyncBar()
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

    fun onDeleteClick(item: Meal) = showDialog {
        AlertDialog.Builder(activity)
                .setMessage("Czy na pewno chcesz usunąć posiłek?")
                .setNegativeButton("Anuluj") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Usuń") { _, _ ->
                    fbSaver.removeMeal(item)
                    activity.startService<RefreshIngredientsDataService>()
                    view?.showSyncBar()
                }
                .create()
    }

    fun onEditClick(item: Meal) = navigator.goTo(AddMealScreen(item.type, item))

    fun onAddIngredientClick() = navigator.goTo(AddIngredientScreen())

    fun onLabelClick(meals: List<Meal>) = navigator.goTo(PieChartScreen(meals.map { it.id }))

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMealsUpdated(ev: MealsUpdatedEvent) {
        info { "[${Thread.currentThread()}] onMealsUpdated()" }
        updateData()
    }

    private fun updateData() {
        launch {
            if (!App.isSyncing) {
                withContext(uiContext) { view.hideSyncBar() }
            }
            delay(1000)
            val meals = queryMeals()
            withContext(uiContext) { view?.showMeals(meals) }
        }
    }

    private fun goToNewMealScreen(type: MealType) {
        view?.meals?.hide()
        navigator.goTo(AddMealScreen(type))
    }

    private fun Meal.updateTimestamp(newValue: Long) {
        fbSaver.updateMealTime(id, newValue)
    }

}