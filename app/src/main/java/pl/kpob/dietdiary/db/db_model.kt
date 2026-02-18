package pl.kpob.dietdiary.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Created by kpob on 20.10.2017.
 */
@Entity(tableName = "IngredientDTO")
data class IngredientDTO(
        @PrimaryKey val id: String = "",
        val name: String = "",
        val mtc: Float = 0f,
        val lct: Float = 0f,
        val carbohydrates: Float = 0f,
        val protein: Float = 0f,
        val salt: Float = 0f,
        val roughage: Float = 0f,
        val calories: Float = 0f,
        val category: Int = 0,
        val useCount: Int = 0
)

@Entity(tableName = "MealDTO")
data class MealDTO(
        @PrimaryKey val id: String = "",
        val time: Long = 0L,
        val name: String = ""
)

@Entity(
        tableName = "MealIngredientDTO",
        primaryKeys = ["mealId", "ingredientId"],
        foreignKeys = [ForeignKey(
                entity = MealDTO::class,
                parentColumns = ["id"],
                childColumns = ["mealId"],
                onDelete = ForeignKey.CASCADE
        )],
        indices = [Index("mealId")]
)
data class MealIngredientDTO(
        val mealId: String = "",
        val ingredientId: String = "",
        val weight: Float = 0f
)

@Entity(tableName = "TagDTO")
data class TagDTO(
        @PrimaryKey val id: String = "",
        val creationTime: Long = 0L,
        val name: String = "",
        val color: Int = 0,
        val activeColor: Int = 0,
        val textColor: Int = 0,
        val activeTextColor: Int = 0
)

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
