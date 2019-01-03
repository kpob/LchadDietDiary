package pl.kpob.dietdiary.sharedcode.utils.templates

import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.MealTemplate
import pl.kpob.dietdiary.sharedcode.model.MealType

interface TemplateManager {

    fun loadTemplates() : List<MealTemplate>
    fun addTemplate(name: String, ingredients: List<Ingredient>)
    fun loadTemplateByMealType(type: MealType): List<MealTemplate>
    fun exists(name: String): Boolean
}