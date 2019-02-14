package pl.kpob.dietdiary.sharedcode.viewmodel.addmeal

import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.MealPart

sealed class AddMealItem

class IngredientMealItem: AddMealItem() {
    var weight: Float = .0f
    var ingredient: Ingredient? = null

    val isValid: Boolean get() = ingredient != null

    val mealPart: MealPart get() = MealPart(ingredient!!, weight)
}

object NextButtonItem: AddMealItem()
class SummaryItem: AddMealItem() {
    var left: Float = .0f
}

class AddMealViewModel(val ingredients: List<Ingredient>) {

    private var dataItems: MutableList<IngredientMealItem> = mutableListOf()
    private val summaryItem = SummaryItem()
    private val nextItem = NextButtonItem

    fun addRow(): Int {
        dataItems.add(IngredientMealItem())
        return dataItems.size - 1
    }

    fun removeRowAt(position: Int) {
        dataItems.removeAt(position)
    }

    fun itemTypeAt(position: Int): AddMealItem {
        return items[position]
    }

    fun updateItemAt(position: Int, weight: Float): Float  {
        dataItems[position].weight = weight
        return weight
    }

    fun updateItemAt(position: Int, ingredient: Ingredient)  {
        dataItems[position].ingredient = ingredient
    }

    fun updateSummary(weight: Float) {
        summaryItem.left = weight
    }

    val weight: Float get() = dataItems.map { it.weight }.sum()
    val left: Float get() = summaryItem.left

    val items: List<AddMealItem> get() {
        return mutableListOf<AddMealItem>().apply {
            addAll(dataItems)
            add(nextItem)
            add(summaryItem)
        }
    }

    val mealParts: List<MealPart> get() = dataItems.filter { it.isValid }.map { it.mealPart }


    val count: Int get() = dataItems.size + 2

}