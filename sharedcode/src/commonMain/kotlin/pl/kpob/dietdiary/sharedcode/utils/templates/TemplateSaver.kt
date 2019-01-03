package pl.kpob.dietdiary.sharedcode.utils.templates

import pl.kpob.dietdiary.sharedcode.model.MealTemplateDTO

interface TemplateSaver {

    fun save(template: MealTemplateDTO)
}