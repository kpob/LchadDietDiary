package pl.kpob.dietdiary.firebase

import io.realm.RealmList
import pl.kpob.dietdiary.AppPrefs
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.db.MealIngredientDTO


/**
 * Created by kpob on 22.10.2017.
 */
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
