package pl.kpob.dietdiary.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import pl.kpob.mapper_annotation.Ignore
import pl.kpob.mapper_annotation.MapAs
import pl.kpob.mapper_annotation.AutoMapping

/**
 * Created by kpob on 20.10.2017.
 */
open class RealmString(
    open var string: String = ""
): RealmObject()

@AutoMapping
open class IngredientDTO(
        @PrimaryKey open var id: String = "",
        open var name: String = "",
        open var mtc: Float = .0f,
        open var lct: Float = .0f,
        open var carbohydrates: Float = .0f,
        open var protein: Float = .0f,
        open var salt: Float = .0f,
        open var roughage: Float = .0f,
        open var calories: Float = .0f,
        open var category: Int = 0,
        open var useCount: Int = 0
): RealmObject()

@AutoMapping(generateDomainModel = false, generateFirebaseModel = false, generateRepository = false, generateContract = true)
open class MealDTO(
        @PrimaryKey open var id: String = "",
        open var time: Long = 0L,
        open var name: String = "",
        open var ingredients: RealmList<MealIngredientDTO> = RealmList()
): RealmObject()

@AutoMapping(generateDomainModel = false, generateRepository = false)
open class MealIngredientDTO(
        open var ingredientId: String = "",
        open var weight: Float = .0f
): RealmObject()

@AutoMapping
open class TagDTO(
        @PrimaryKey open var id: String = "",
        @Ignore
        open var creationTime: Long = 0L,
        @MapAs(mapAs = "tagName")
        open var name: String = "",
        open var color: Int = 0,
        open var activeColor: Int = 0,
        open var textColor: Int = 0,
        open var activeTextColor: Int = 0
): RealmObject()

@AutoMapping(generateDomainModel = false, generateFirebaseModel = false, generateRepository = false)
open class MealTemplateDTO(
        @PrimaryKey open var id: String = "",
        open var name: String = "",
        open var type: String = "",
        open var ingredientIds: RealmList<RealmString> = RealmList()
): RealmObject()

enum class IngredientCategory(val value: Int, val label: String) {
    PORRIDGE(1, "Kaszka"),
    FRUITS(2, "Owocki"),
    DINNERS(3, "Obiadki"),
    OTHERS(4, "Inne"),
    OILS(5, "Oleje"),
    FRUITS_TUBE(6, "Owocowe tubki"),
    DIARY(7, "Nabiał");

    companion object {
        fun fromInt(i: Int) = values().firstOrNull { it.value == i } ?: PORRIDGE
        fun fromString(s: String) = values().firstOrNull { it.label == s } ?: PORRIDGE

        fun stringValues() = values().map { it.label }
    }
}
