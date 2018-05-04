package pl.kpob.dietdiary.screens.utils

import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.firebase.FbIngredient

/**
 * Created by kpob on 16.03.2018.
 */
object Traits {

    fun mealInteractor(type: MealType, meal: Meal?): MealDataInteractor = IMealDataInteractor(type, meal)
    fun ingredientInteractor(ingredient: FbIngredient?): IngredientDataInteractor = IIngredientDataInteractor(ingredient)
    fun timePickerCreator(): TimePickerCreator = ITimePickerCreator()
}