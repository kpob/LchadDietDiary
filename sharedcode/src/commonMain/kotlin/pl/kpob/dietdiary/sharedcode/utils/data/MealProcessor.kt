package pl.kpob.dietdiary.sharedcode.utils

import pl.kpob.dietdiary.sharedcode.model.IngredientUsage
import pl.kpob.dietdiary.sharedcode.model.MealPart

object MealProcessor {


    fun process(data: List<MealPart>, left: Float): List<MealPart> {
        val totalWeight = data.map { it.weight }.sum()
        val percentage = (totalWeight-left)/totalWeight


        return data.map {
            MealPart(it.ingredient, it.weight * percentage)
        }
    }

    fun calculateUsage(data: List<MealPart>): List<IngredientUsage> =
            data.map { it.ingredient }.map { IngredientUsage(it.id, it.useCount + 1) }
}