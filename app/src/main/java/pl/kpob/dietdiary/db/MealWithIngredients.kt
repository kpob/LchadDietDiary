package pl.kpob.dietdiary.db

import androidx.room.Embedded
import androidx.room.Relation

data class MealWithIngredients(
    @Embedded val meal: MealDTO,
    @Relation(
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val ingredients: List<MealIngredientDTO>
)
