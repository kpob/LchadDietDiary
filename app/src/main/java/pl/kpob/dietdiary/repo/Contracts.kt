package pl.kpob.dietdiary.repo

object IngredientContract {
    const val TABLE_NAME = "IngredientDTO"
    const val ID = "id"
    const val NAME = "name"
    const val MTC = "mtc"
    const val LCT = "lct"
    const val CARBOHYDRATES = "carbohydrates"
    const val PROTEIN = "protein"
    const val SALT = "salt"
    const val ROUGHAGE = "roughage"
    const val CALORIES = "calories"
    const val CATEGORY = "category"
    const val USE_COUNT = "useCount"
}

object MealContract {
    const val TABLE_NAME = "MealDTO"
    const val ID = "id"
    const val TIME = "time"
    const val NAME = "name"
    const val INGREDIENTS = "ingredients"
}

object MealIngredientContract {
    const val TABLE_NAME = "MealIngredientDTO"
    const val INGREDIENT_ID = "ingredientId"
    const val WEIGHT = "weight"
}

object TagContract {
    const val TABLE_NAME = "TagDTO"
    const val ID = "id"
    const val CREATION_TIME = "creationTime"
    const val NAME = "name"
    const val COLOR = "color"
    const val ACTIVE_COLOR = "activeColor"
    const val TEXT_COLOR = "textColor"
    const val ACTIVE_TEXT_COLOR = "activeTextColor"
}
