package pl.kpob.dietdiary.sharedcode.presenter

import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.eventbus.DietDiaryEventBus
import pl.kpob.dietdiary.sharedcode.eventbus.MealsUpdateEventReceiver
import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.model.MealType
import pl.kpob.dietdiary.sharedcode.repository.AllMealsSortedSpecification
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.utils.AppSyncState
import pl.kpob.dietdiary.sharedcode.utils.MyDateTime
import pl.kpob.dietdiary.sharedcode.view.AppNavigator
import pl.kpob.dietdiary.sharedcode.view.MainView
import pl.kpob.dietdiary.sharedcode.view.PopupData
import pl.kpob.dietdiary.sharedcode.view.popup.*
import pl.kpob.dietdiary.sharedcode.viewmodel.MealDataMapper
import pl.kpob.dietdiary.sharedcode.viewmodel.MealsViewModel

class MainPresenter(
        private val mealsRepository: Repository<MealDTO, Meal>,
        private val remoteDatabase: RemoteDatabase,
        private val appNavigator: AppNavigator,
        private val eventBus: DietDiaryEventBus,
        private val mealsUpdateEventReceiver: MealsUpdateEventReceiver,
        private val appSyncState: AppSyncState,
        private val popupDisplayer: PopupDisplayer
) {

    var view: MainView? = null

    private val isAppSyncing: Boolean get() = appSyncState.isSyncing

    private fun queryMeals(): MealsViewModel {
        val meals = mealsRepository.query(AllMealsSortedSpecification())

        return MealDataMapper.mealsAsViewModel(meals)
    }

    fun onShow(view: MainView) {
        this.view = view
        mealsUpdateEventReceiver.syncView = view
        eventBus.register(mealsUpdateEventReceiver)

//        val intent = activity.intent
//        if (intent.hasExtra(MainActivity.EXTRA_MEAL)) {
//            val mealType = MealType.fromString(intent.getStringExtra(MainActivity.EXTRA_MEAL))
//            intent.removeExtra(MainActivity.EXTRA_MEAL)
//            appNavigator.goToAddMealView(mealType)
//        }
        val meals = queryMeals()
        view.showMeals(meals)
    }

    fun onHide() {
        eventBus.unregister(mealsUpdateEventReceiver)
        view = null
    }

    fun onResume() {
        if (isAppSyncing) view?.showSyncBar()
    }

    fun onDessertClick() = goToNewMealView(MealType.DESSERT)

    fun onDinnerClick() = goToNewMealView(MealType.DINNER)

    fun onMilkClick() = goToNewMealView(MealType.MILK)

    fun onItemClick(item: Meal) = appNavigator.goToPieChartView(item.id)

    fun onTimeClick(item: Meal) {
        val viewModel = EditTimePopup(PopupData(), PopupCallbacks.init {
            ok("Zmień") { payload ->
                if (payload is EditMealTimePayload) {
                    updateMealTimestamp(item, payload)
                }
            }

            cancel("Anuluj") {}
        })

        popupDisplayer.display(viewModel)
    }

    fun onDeleteClick(item: Meal) {
        val data = PopupData(messages = "Czy na pewno chcesz usunąć posiłek?")
        val callbacks = PopupCallbacks.init {
            ok("Usuń") {
                remoteDatabase.removeMeal(item)
                view?.showSyncBar()
            }

            cancel("Anuluj") {  }
        }

        popupDisplayer.display(DeleteMealPopup(data, callbacks))
    }

    fun onEditClick(item: Meal) = appNavigator.goToEditMealView(item.type, item)

    fun onAddIngredientClick() = appNavigator.goToAddIngredientView()

    fun onLabelClick(meals: List<Meal>) = appNavigator.goToPieChartView(meals.map { it.id })

    fun updateData() {
        if (!isAppSyncing) {
            view?.hideSyncBar()
        }
        val meals = queryMeals()
        view?.showMeals(meals)
    }

    private fun goToNewMealView(type: MealType) {
        view?.hideMeals()
        appNavigator.goToAddMealView(type)
    }

    private fun updateMealTimestamp(meal: Meal, payload: EditMealTimePayload) {
        val newTimestamp = MyDateTime(meal.timestamp)
                .withTime(payload.hour, payload.minute)
                .timestamp
        remoteDatabase.updateMealTime(meal.id, newTimestamp)
    }

}