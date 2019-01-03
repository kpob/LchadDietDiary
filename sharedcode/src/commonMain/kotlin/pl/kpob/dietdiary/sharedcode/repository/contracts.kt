package pl.kpob.dietdiary.sharedcode.repository

import pl.kpob.dietdiary.sharedcode.model.MealDTO

object MealContract {
    const val ID = "id"
    const val TIME = "time"
    const val INGREDIENTS = "ingredients"
}

object MealIngredientContract {
    const val INGREDIENT_ID = "ingredient_id"
}

object IngredientContract {
    const val ID = "id"
    const val CATEGORY = "category"
    const val USE_COUNT = "useCount"
    const val NAME = "name"
}

object MealTemplateContract {
    const val ID = "id"
    const val TYPE = "type"
}