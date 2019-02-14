package pl.kpob.dietdiary.sharedcode.utils

import pl.kpob.dietdiary.sharedcode.model.*

object MockDataProvider {


    fun provideMeals(): List<Meal> = listOf(
            Meal("1", "16:21", "31-12-2018", MealType.MILK, 37.4f, 0.36f, 365, 2018, 2121534, false),
            Meal("2", "15:44", "31-12-2018", MealType.DESSERT, 44.4f, 0.16f, 365, 2018, 2121534, false),
            Meal("3", "15:28", "31-12-2018", MealType.DINNER, 93.5f, 1.14f, 365, 2018, 2121534, false),
            Meal("4", "12:43", "31-12-2018", MealType.MILK, 231.8f, 2.04f, 365, 2018, 2121534, false),
            Meal("6", "08:50", "31-12-2018", MealType.DESSERT, 257.56f, 0.31f, 365, 2018, 2121534, false),
            Meal("7", "08:50", "31-12-2018", MealType.MILK, 138.3f, 1.14f, 365, 2018, 2121534, false),
            Meal("8", "20:24", "30-12-2018", MealType.MILK, 46.1f, 0.38f, 364, 2018, 2121534, false),
            Meal("9", "18:55", "30-12-2018", MealType.DESSERT, 255.53f, 0.32f, 364, 2018, 2121534, false),
            Meal("10", "17:03", "30-12-2018", MealType.MILK, 93.5f, 0.9f, 364, 2018, 2121534, false),
            Meal("11", "15:52", "30-12-2018", MealType.MILK, 138.3f, 1.14f, 364, 2018, 2121534, false),
            Meal("12", "15:52", "30-12-2018", MealType.DESSERT, 51.45f, 0.27f, 364, 2018, 2121534, false),
            Meal("13", "13:20", "30-12-2018", MealType.DINNER, 133.47f, 1.79f, 364, 2018, 2121534, false),
            Meal("14", "08:39", "30-12-2018", MealType.DESSERT, 262.61f, 0.5f, 364, 2018, 2121534, false),
            Meal("15", "08:39", "30-12-2018", MealType.MILK, 138.3f, 1.14f, 364, 2018, 2121534, false)
//            Meal("16", "17:03", "30-12-2018", MealType.MILK, 138.3f, 1.14f, 364, 2018, 2121534, false),
//            Meal("10", "10:13", "2018-12-11", MealType.DESSERT, 201.0f, 1.1f, 299, 2018, 2121434, false)
    )


    fun provideIngredients(): List<Ingredient> = listOf(
            Ingredient("1", "Pilos protein borówka amerykańska", category = IngredientCategory.FRUITS.value),
            Ingredient("2", "Hip jabłka z brzoskwiniami", category = IngredientCategory.FRUITS.value),
            Ingredient("3", "Bobvita brzoskwinia, jabłka, banany, kiwi", category = IngredientCategory.FRUITS.value),
            Ingredient("4", "Marchewkowa Z Ryżem", category = IngredientCategory.DINNERS.value),
            Ingredient("5", "Kaszka manna owocowa", category = IngredientCategory.PORRIDGE.value, useCount = 31),
            Ingredient("6", "Żarłaki", category = IngredientCategory.OTHERS.value),
            Ingredient("7", "Jabłuszka Z kaszką manną", category = IngredientCategory.FRUITS.value),
            Ingredient("8", "Kaszka Waniliowa", category = IngredientCategory.PORRIDGE.value),
            Ingredient("9", "Jabłka, morele i brzoskwinie z biszkoptem", category = IngredientCategory.FRUITS.value),
            Ingredient("10", "Mleczko (proszek)", category = IngredientCategory.OTHERS.value, useCount = 33),
            Ingredient("11", "Mleko 0.5%", category = IngredientCategory.OTHERS.value, useCount = 40),
            Ingredient("12", "Mleczko (płyn)", category = IngredientCategory.OTHERS.value),
            Ingredient("13", "Olej MCT", category = IngredientCategory.OILS.value),
            Ingredient("14", "\"Rosołek z kurczaka z ryżem", category = IngredientCategory.DINNERS.value)
    ).sortedBy { it.useCount }

}