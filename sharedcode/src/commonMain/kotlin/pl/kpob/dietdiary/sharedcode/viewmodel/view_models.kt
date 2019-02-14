package pl.kpob.dietdiary.sharedcode.viewmodel

import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.utils.asFormattedString


data class MealsGroup(
        val day: Int,
        val meals: List<Meal>
)

data class MealsViewModel(val mealsData: List<MealsGroup>, val groups: Int, val ranges: List<IntRange>) {

    val viewsCount: Int = mealsData.size + mealsData.map { it.meals }.flatten().size

    fun viewTypeByPosition(position: Int): Int {
        ranges.forEach {
            if (it.contains(position)) {
                return MEAL_ITEM_VIEW_TYPE
            }
        }
        return LABEL_VIEW_TYPE
    }

    fun labelViewModelByPosition(position: Int): DayLabelViewModel {
        val meals = mealsData[ranges.indexOfFirst { it.first > position }].meals

        val dateText = if(meals[0].isToday) "Dzisiaj" else meals[0].date
        val totalLtc = meals.map { it.lct }.sum().asFormattedString()
        val lctLabel = "$totalLtc g"
        val totalCalories = meals.map { it.calories }.sum().asFormattedString()
        val caloriesText = "$totalCalories kcal"

        return DayLabelViewModel(meals, dateText, lctLabel, caloriesText)
    }

    fun mealViewModelByPosition(position: Int): MealItemViewModel {
        val rangeIdx = ranges.indexOfFirst { it.contains(position) }

        val item = mealsData.map { it.meals }.flatten()[position - rangeIdx - 1]
        val time = item.time
        val calories = "${item.calories.asFormattedString()} kcal"
        val lct = "${item.lct.asFormattedString()} g"

        return MealItemViewModel(item, time, lct, calories)
    }

    fun isEditable(position: Int): Boolean {
        return viewTypeByPosition(position) == MEAL_ITEM_VIEW_TYPE
    }

    companion object {
        const val LABEL_VIEW_TYPE: Int = 1
        const val MEAL_ITEM_VIEW_TYPE: Int = 2
    }
}

data class DayLabelViewModel(val meals: List<Meal>, val date: String, val lct: String, val calories: String)

data class MealItemViewModel(
        val meal: Meal,
        val time: String,
        val lct: String,
        val calories: String)

data class IngredientsGroups(
        val category: IngredientCategory,
        val ingredients: List<Ingredient>
)

data class IngredientsViewModel(
        val ingredients: List<IngredientsGroups>,
        val groups: Int,
        val ranges: List<IntRange>
) {

    val viewsCount: Int = ingredients.size + ingredients.map { it.ingredients }.flatten().size

    fun viewTypeByPosition(position: Int): Int {
        ranges.forEach {
            if (it.contains(position)) {
                return ITEM_VIEW_TYPE
            }
        }
        return LABEL_VIEW_TYPE
    }

    fun categoryName(position: Int): String {
        val category = ingredients[ranges.indexOfFirst { it.first > position }].category
        return category.label
    }

    fun item(position: Int): Ingredient {
        val rangeIdx = ranges.indexOfFirst { it.contains(position) }
        val item = ingredients.map { it.ingredients }.flatten()[position - rangeIdx - 1]
        return item
    }

    fun isEditable(position: Int): Boolean {
        return viewTypeByPosition(position) == ITEM_VIEW_TYPE
    }

     companion object {
         const val LABEL_VIEW_TYPE = 1
         const val ITEM_VIEW_TYPE = 2
     }
}

data class SummaryTabsViewModel(val ingredients: List<MealIngredient>, val meals: List<MealDetails>) {

    fun tabTitle(position: Int) = TITLES[position]

    val tabsCount: Int get() = TITLES.size

    val nutrients: Map<String, Float> by lazy { mapOf(
            "Węglowodany" to meals.nutrientSum { it.carbohydrates },
            "Mct" to meals.nutrientSum { it.mtc },
            "Lct" to meals.nutrientSum { it.lct },
            "Białko" to meals.nutrientSum { it.protein },
            "Błonnik" to meals.nutrientSum { it.roughage },
            "Sól" to meals.nutrientSum { it.salt }
        )
    }

    private inline fun List<MealDetails>.nutrientSum(f: (MealDetails) -> Float): Float = map { f(it) }.sum()

    companion object {
        private val TITLES = arrayOf("Składniki", "Wykres")
    }

}



class TagsViewModel {

    val initialTags = arrayOf("Obiadki", "Deserki", "Mleczko", "Nabiał", "Oleje", "Inne")
}