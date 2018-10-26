package pl.kpob.dietdiary.mapper

import pl.kpob.dietdiary.db.MealTemplateDTO
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.MealTemplate
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.repo.Mapper

/**
 * Created by kpob on 27.05.2018.
 */
class MealTemplateMapper(private val ingredients: List<Ingredient>): Mapper<MealTemplateDTO, MealTemplate> {
    override fun map(input: MealTemplateDTO?): MealTemplate? {
        val ids = input?.ingredientIds?.map { it.string } ?: listOf()
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