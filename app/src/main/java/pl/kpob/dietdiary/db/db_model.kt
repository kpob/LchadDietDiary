package pl.kpob.dietdiary.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import pl.kpob.dietdiary.sharedcode.model.*

/**
 * Created by kpob on 20.10.2017.
 */
open class RealmString(
    open var string: String = ""
): RealmObject()

open class RealmIngredient(
        @PrimaryKey override var id: String = "",
        override var name: String = "",
        override var mtc: Float = .0f,
        override var lct: Float = .0f,
        override var carbohydrates: Float = .0f,
        override var protein: Float = .0f,
        override var salt: Float = .0f,
        override var roughage: Float = .0f,
        override var calories: Float = .0f,
        override var category: Int = 0,
        override var useCount: Int = 0
): RealmObject(), IngredientDTO

open class RealmMeal(
        @PrimaryKey override var id: String = "",
        override var time: Long = 0L,
        override var name: String = "",
        open var realmIngredients: RealmList<RealmMealIngredient> = RealmList()
): RealmObject(), MealDTO {
        override var ingredients: List<MealIngredientDTO>
                get() = realmIngredients
                set(value) {
                        val l = RealmList<RealmMealIngredient>()
                        l.addAll(value as List<RealmMealIngredient>)
                        realmIngredients = l
                }
}

open class RealmMealIngredient(
        override var ingredientId: String = "",
        override var weight: Float = .0f
): RealmObject(), MealIngredientDTO

open class RealmTag(
        @PrimaryKey override var id: String = "",
        override var creationTime: Long = 0L,
        override var name: String = "",
        override var color: Int = 0,
        override var activeColor: Int = 0,
        override var textColor: Int = 0,
        override var activeTextColor: Int = 0
): RealmObject(), TagDTO

open class RealmMealTemplate(
        @PrimaryKey override var id: String = "",
        override var name: String = "",
        override var type: String = "",
        open var realmIngredientIds: RealmList<RealmString> = RealmList()
): RealmObject(), MealTemplateDTO {

        override var ingredientIds: List<String>
                get() = realmIngredientIds.map { it.string }
                set(value) {
                        val l = RealmList<RealmString>()
                        l.addAll(value.map { RealmString(it) })
                        realmIngredientIds = l
                }
}
