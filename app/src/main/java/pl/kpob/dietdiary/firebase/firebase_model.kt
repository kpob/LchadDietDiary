package pl.kpob.dietdiary.firebase

import pl.kpob.dietdiary.AppPrefs
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.db.MealIngredientDTO
import pl.kpob.dietdiary.db.TagDTO


/**
 * Created by kpob on 22.10.2017.
 */
interface FirebaseModel {
    val deleted: Boolean
}

data class FbMeal(
        val id: String = "",
        val time: Long = 0L,
        val name: String = "",
        val ingredients: List<FbMealIngredient> = listOf(),
        override val deleted: Boolean = false,
        val senderToken: String = AppPrefs.token
) : FirebaseModel {
    fun toDtoPair(): Pair<MealDTO, List<MealIngredientDTO>> {
        val meal = MealDTO(id = id, time = time, name = name)
        val ingredientDtos = ingredients.map { it.toDto(id) }
        return meal to ingredientDtos
    }
}

data class FbMealIngredient(
        val ingredientId: String = "",
        val weight: Float = 0f,
        override val deleted: Boolean = false
) : FirebaseModel {
    fun toDto(mealId: String) = MealIngredientDTO(
            mealId = mealId,
            ingredientId = ingredientId,
            weight = weight
    )
}

data class FbIngredient(
        val id: String = "",
        val name: String = "",
        val mtc: Float = 0f,
        val lct: Float = 0f,
        val carbohydrates: Float = 0f,
        val protein: Float = 0f,
        val salt: Float = 0f,
        val roughage: Float = 0f,
        val calories: Float = 0f,
        val category: Int = 0,
        val useCount: Int = 0,
        override val deleted: Boolean = false
) : FirebaseModel {
    fun toDto() = IngredientDTO(
            id = id, name = name, mtc = mtc, lct = lct,
            carbohydrates = carbohydrates, protein = protein, salt = salt,
            roughage = roughage, calories = calories, category = category, useCount = useCount
    )
}

data class FbTag(
        val id: String = "",
        val creationTime: Long = 0L,
        val name: String = "",
        val color: Int = 0,
        val activeColor: Int = 0,
        val textColor: Int = 0,
        val activeTextColor: Int = 0,
        override val deleted: Boolean = false
) : FirebaseModel {
    fun toDto() = TagDTO(
            id = id, creationTime = creationTime, name = name,
            color = color, activeColor = activeColor,
            textColor = textColor, activeTextColor = activeTextColor
    )
}
