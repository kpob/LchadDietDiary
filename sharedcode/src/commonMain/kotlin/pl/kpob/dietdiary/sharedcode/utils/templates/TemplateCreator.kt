package pl.kpob.dietdiary.sharedcode.utils.templates

import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.MealTemplateDTO
import pl.kpob.dietdiary.sharedcode.model.MealType

interface TemplateCreator {

    fun create(name: String, mealType: MealType, ingredients: List<Ingredient>): MealTemplateDTO
}