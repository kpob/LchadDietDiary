package pl.kpob.dietdiary.utils

import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.IngredientUsage
import pl.kpob.dietdiary.domain.MealPart

object MealProcessor {

    fun process(data: List<Pair<Ingredient, Float>>, left: Float): List<MealPart> {
        val totalWeight = data.map { it.second }.sum()
        val percentage = (totalWeight-left)/totalWeight


        return data.map {
            MealPart(it.first, it.second * percentage)
        }
    }

    fun calculateUsage(data: List<Pair<Ingredient, Float>>): List<IngredientUsage> =
        data.map { it.first }.map { IngredientUsage(it.id, it.useCount + 1) }

}