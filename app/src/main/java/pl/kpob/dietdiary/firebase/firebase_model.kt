package pl.kpob.dietdiary.firebase

import io.realm.RealmList
import io.realm.RealmObject
import pl.kpob.dietdiary.AppPrefs
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.db.MealIngredientDTO
import pl.kpob.dietdiary.db.TagDTO


/**
 * Created by kpob on 22.10.2017.
 */
interface FirebaseModel<T : RealmObject> {
    fun toRealm(): T
    val deleted: Boolean
}

data class FbMeal(
        val id: String = "",
        val time: Long = 0L,
        val name: String = "",
        val ingredients: List<FbMealIngredient> = listOf(),
        override val deleted: Boolean = false,
        val senderToken: String = AppPrefs.token
) : FirebaseModel<MealDTO> {
    override fun toRealm() = MealDTO(id, time, name, RealmList<MealIngredientDTO>().apply { addAll(ingredients.map { it.toRealm() }) })
}

data class FbMealIngredient(
        val ingredientId: String = "",
        val weight: Float = 0f,
        override val deleted: Boolean = false
) : FirebaseModel<MealIngredientDTO> {
    override fun toRealm() = MealIngredientDTO(ingredientId = ingredientId, weight = weight)
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
) : FirebaseModel<IngredientDTO> {
    override fun toRealm() = IngredientDTO(
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
) : FirebaseModel<TagDTO> {
    override fun toRealm() = TagDTO(
            id = id, creationTime = creationTime, name = name,
            color = color, activeColor = activeColor,
            textColor = textColor, activeTextColor = activeTextColor
    )
}
