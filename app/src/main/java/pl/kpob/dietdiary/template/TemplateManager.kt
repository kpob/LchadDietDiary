package pl.kpob.dietdiary.template

import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.MealTemplate
import pl.kpob.dietdiary.domain.MealType

/**
 * Created by kpob on 29.07.2018.
 */
interface TemplateManager {

    fun loadTemplates() : List<MealTemplate>
    fun addTemplate(name: String, ingredients: List<Ingredient>)
    fun loadTemplateByMealType(type: MealType): List<MealTemplate>
    fun exists(name: String): Boolean
}