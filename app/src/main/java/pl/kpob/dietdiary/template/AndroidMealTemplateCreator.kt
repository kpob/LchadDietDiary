package pl.kpob.dietdiary.template

import io.realm.RealmList
import pl.kpob.dietdiary.db.RealmMealTemplate
import pl.kpob.dietdiary.db.RealmString
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.MealTemplateDTO
import pl.kpob.dietdiary.sharedcode.model.MealType
import pl.kpob.dietdiary.sharedcode.utils.nextId
import pl.kpob.dietdiary.sharedcode.utils.templates.TemplateCreator

object AndroidMealTemplateCreator: TemplateCreator {

    override fun create(name: String, mealType: MealType, ingredients: List<Ingredient>): MealTemplateDTO {
        return RealmMealTemplate(
                id = nextId(),
                name = name,
                type = mealType.string,
                realmIngredientIds = RealmList<RealmString>().apply { addAll(ingredients.map { RealmString(it.id) }) }
        )
    }
}