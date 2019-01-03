package pl.kpob.dietdiary.utils

import com.wealthfront.magellan.Navigator
import pl.kpob.dietdiary.screens.AddIngredientScreen
import pl.kpob.dietdiary.screens.AddMealScreen
import pl.kpob.dietdiary.screens.PieChartScreen
import pl.kpob.dietdiary.screens.RangeSummaryScreen
import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.model.MealType
import pl.kpob.dietdiary.sharedcode.utils.Day
import pl.kpob.dietdiary.sharedcode.view.AppNavigator

class AndroidAppNavigator(private val navigator: Navigator): AppNavigator {

    override fun goBack() {
        navigator.goBack()
    }

    override fun goToAddMealView(type: MealType) {
        navigator.goTo(AddMealScreen(type))
    }

    override fun goToEditMealView(type: MealType, meal: Meal) {
        navigator.goTo(AddMealScreen(type, meal))
    }

    override fun goToAddIngredientView() {
        navigator.goTo(AddIngredientScreen())
    }

    override fun goToPieChartView(ids: List<String>) {
        navigator.goTo(PieChartScreen(ids))
    }

    override fun goToPieChartView(id: String) {
        navigator.goTo(PieChartScreen(id))
    }

    override fun goToRangeSummary(first: Day, last: Day) {
        navigator.goTo(RangeSummaryScreen(first, last))
    }

}