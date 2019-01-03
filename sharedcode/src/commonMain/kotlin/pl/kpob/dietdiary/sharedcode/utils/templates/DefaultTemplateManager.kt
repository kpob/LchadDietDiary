package pl.kpob.dietdiary.sharedcode.utils.templates

import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.MealTemplate
import pl.kpob.dietdiary.sharedcode.model.MealTemplateDTO
import pl.kpob.dietdiary.sharedcode.model.MealType
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.repository.TemplatesByMealType

class DefaultTemplateManager(
        private val repo: Repository<MealTemplateDTO, MealTemplate>,
        private val creator: TemplateCreator,
        private val saver: TemplateSaver,
        private val type: MealType
): TemplateManager {


    private val templates: List<MealTemplate>
        get() = repo.query(TemplatesByMealType(type))

    override fun loadTemplates(): List<MealTemplate> = templates

    override fun addTemplate(name: String, ingredients: List<Ingredient>) {
        val template = creator.create(name, type, ingredients)
        saver.save(template)
    }

    override fun exists(name: String): Boolean = templates.map { it.name }.any { it == name }

    override fun loadTemplateByMealType(type: MealType): List<MealTemplate> =
            repo.query(TemplatesByMealType(type))


}