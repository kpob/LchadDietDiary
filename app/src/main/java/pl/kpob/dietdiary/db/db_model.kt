package pl.kpob.dietdiary.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import pl.kpob.dietdiary.server.FbIngredient

/**
 * Created by kpob on 20.10.2017.
 */
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
): RealmObject() {

    override fun toString(): String {
        return name
    }

    fun toFirebase(deleted: Boolean = false): FbIngredient = FbIngredient(
            id, name, mtc, lct, carbohydrates, protein, salt, roughage, calories, category, deleted
    )
}

open class MealDTO(
        @PrimaryKey open var id: String = "",
        open var time: Long = 0L,
        open var name: String = "",
        open var ingredients: RealmList<MealIngredientDTO> = RealmList()
): RealmObject()

open class MealIngredientDTO(
        open var ingredientId: String = "",
        open var weight: Float = .0f
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