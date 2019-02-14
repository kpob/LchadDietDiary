package pl.kpob.dietdiary.sharedcode.view

import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.model.MealType
import pl.kpob.dietdiary.sharedcode.utils.Day

interface AppNavigator {

    fun goToAddMealView(type: MealType)

    fun goToEditMealView(type: MealType, meal: Meal)

    fun goToAddIngredientView(item: FbIngredient? = null)

    fun goToPieChartView(ids: List<String>)
    fun goToPieChartView(id: String)
    fun goBack()
    fun goToRangeSummary(first: Day, last: Day)
    fun goToIngredientList()
}