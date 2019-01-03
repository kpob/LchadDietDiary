package pl.kpob.dietdiary.sharedcode.model

import kotlinx.serialization.Serializable
import pl.kpob.dietdiary.sharedcode.utils.UserTokenProvider

interface FirebaseModel {
    val deleted: Boolean
}

@Serializable
data class FbMealIngredient(
        val ingredientId: String = "",
        val weight: Float = .0f,
        override val deleted: Boolean = false): FirebaseModel

@Serializable
data class FbIngredient(
        val id: String = "",
        val name: String = "",
        val mtc: Float = .0f,
        val lct: Float = .0f,
        val carbohydrates: Float = .0f,
        val protein: Float = .0f,
        val salt: Float = .0f,
        val roughage: Float = .0f,
        val calories: Float = .0f,
        val category: Int = 0,
        val useCount: Int = 0, override val deleted: Boolean = false): FirebaseModel

@Serializable
data class FbMeal(
        val id: String = "",
        val time: Long = 0L,
        val name: String = "",
        val ingredients: List<FbMealIngredient> = listOf(),
        val kcal: Float = 0.0f,
        val senderToken: String = "",
        override val deleted: Boolean = false
) : FirebaseModel {

    companion object {
        fun withToken(id: String, time: Long, name: String, ingredients: List<FbMealIngredient>, kcal: Float, tokenProvider: UserTokenProvider): FbMeal =
                FbMeal(id, time, name, ingredients, kcal, tokenProvider.token)

    }
}

