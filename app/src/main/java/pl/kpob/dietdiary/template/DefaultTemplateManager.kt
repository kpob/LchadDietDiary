package pl.kpob.dietdiary.template

import io.realm.RealmList
import pl.kpob.dietdiary.db.MealTemplateDTO
import pl.kpob.dietdiary.db.RealmString
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.MealTemplate
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.nextId
import pl.kpob.dietdiary.repo.MealTemplateRepository
import pl.kpob.dietdiary.repo.TemplatesByMealType
import pl.kpob.dietdiary.usingRealm

/**
 * Created by kpob on 29.07.2018.
 */
class DefaultTemplateManager(
        private val repo: MealTemplateRepository,
        private val type: MealType
): TemplateManager {


    private val templates: List<MealTemplate>
        get() = repo.withRealmQuery { TemplatesByMealType(it, type) }

    override fun loadTemplates(): List<MealTemplate> = templates

    override fun addTemplate(name: String, ingredients: List<Ingredient>) {
        usingRealm {
            it.executeTransaction {
                val template = createMealTemplateDTO(name, ingredients)
                it.insertOrUpdate(template)
            }
        }
    }

    override fun exists(name: String): Boolean = templates.map { it.name }.any { it == name }

    override fun loadTemplateByMealType(type: MealType): List<MealTemplate> =
            repo.withRealmQuery { TemplatesByMealType(it, type) }


    private fun createMealTemplateDTO(name: String, ingredients: List<Ingredient>) = MealTemplateDTO(
            id = nextId(),
            name = name,
            type = type.string,
            ingredientIds = RealmList<RealmString>().apply { addAll(ingredients.map { RealmString(it.id) }) }
    )
}