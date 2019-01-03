package pl.kpob.dietdiary.sharedcode.repository.mapper

import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.MealTemplate
import pl.kpob.dietdiary.sharedcode.model.MealTemplateDTO
import pl.kpob.dietdiary.sharedcode.model.MealType

class MealTemplateMapper(private val ingredients: List<Ingredient>): Mapper<MealTemplateDTO, MealTemplate> {
    override fun map(input: MealTemplateDTO?): MealTemplate? {
        val ids = input?.ingredientIds?.map { it } ?: listOf()
        return if (input == null) null else MealTemplate(
                id = input.id,
                type = MealType.fromString(input.type),
                name = input.name,
                ingredients = filterIngredients(ids)
        )
    }

    override fun map(input: List<MealTemplateDTO>): List<MealTemplate> =
            input.map { map(it)!! }

    private fun filterIngredients(ids: List<String>): List<Ingredient> =
            ingredients.filter { ids.contains(it.id) }
}