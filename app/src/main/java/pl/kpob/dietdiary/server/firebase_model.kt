package pl.kpob.dietdiary.server

import io.realm.RealmList
import pl.kpob.dietdiary.AppPrefs
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.db.MealIngredientDTO


/**
 * Created by kpob on 22.10.2017.
 */
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
        val deleted: Boolean = false,
        val useCount: Int = 0
) {
    fun toRealm() = IngredientDTO(id, name, mtc, lct, carbohydrates, protein, salt, roughage, calories, category, useCount)
}

data class FbMeal(
        val id: String = "",
        val time: Long = 0L,
        val name: String = "",
        val ingredients: List<FbMealIngredient> = listOf(),
        val deleted: Boolean = false,
        val senderToken: String = AppPrefs.token
) {
    fun toRealm() = MealDTO(id, time, name, RealmList<MealIngredientDTO>().apply { addAll(ingredients.map { it.toRealm() }) })
}
data class FbMealIngredient(
        val ingredientId: String = "",
        val weight: Float = .0f,
        val deleted: Boolean = false
) {
    fun toRealm() = MealIngredientDTO(ingredientId, weight)
}
