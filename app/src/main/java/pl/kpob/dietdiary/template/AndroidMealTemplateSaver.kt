package pl.kpob.dietdiary.template

import pl.kpob.dietdiary.db.RealmMealTemplate
import pl.kpob.dietdiary.sharedcode.model.MealTemplateDTO
import pl.kpob.dietdiary.sharedcode.utils.templates.TemplateSaver
import pl.kpob.dietdiary.usingRealm

object AndroidMealTemplateSaver: TemplateSaver {

    override fun save(template: MealTemplateDTO) {
        usingRealm {
            it.executeTransaction {
                it.insertOrUpdate(template as RealmMealTemplate)
            }
        }
    }
}