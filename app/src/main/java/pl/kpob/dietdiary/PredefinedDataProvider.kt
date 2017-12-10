package pl.kpob.dietdiary

import pl.kpob.dietdiary.db.IngredientCategory
import pl.kpob.dietdiary.server.FbIngredient

/**
 * Created by kpob on 27.10.2017.
 */
object PredefinedDataProvider {

    val data: List<FbIngredient>
        get() = listOf(
            FbIngredient(nextId(), "Kaszka ryżowa owocowa", .0f, .9f, 86f, 6.9f, .02f, 1.8f, 381f, IngredientCategory.PORRIDGE.value),
            FbIngredient(nextId(), "Kaszka ryżowa", .0f, 1f, 86.5f, 6.5f, .01f, 1f, 383f, IngredientCategory.PORRIDGE.value),
            FbIngredient(nextId(), "Kaszka kukurydziana", .0f, 2f, 86f, 6.9f, .02f, 1.8f, 381f, IngredientCategory.PORRIDGE.value),
            FbIngredient(nextId(), "Dynia z kurczakiem i ziemniakami", .0f, .9f, 8.7f, 2.3f, .03f, 1.1f, 54f, IngredientCategory.DINNERS.value),
            FbIngredient(nextId(), "Spaghetti po bolońsku", .0f, 1.9f, 10f, 3.2f, .05f, 1.5f, 75f, IngredientCategory.DINNERS.value),
            FbIngredient(nextId(), "Rosołek z kurczaka z ryżem", .0f, 1.6f, 7f, 2.1f, .04f, 1f, 53f, IngredientCategory.DINNERS.value),
            FbIngredient(nextId(), "Warzywa z delikatną rybą", .0f, 1.8f, 9.5f, 3f, .1f, 1f, 68f, IngredientCategory.DINNERS.value),
            FbIngredient(nextId(), "Olej MTC", 100f, 0f, 0f, 9f, 0f, 0f, 855f, IngredientCategory.OILS.value),
            FbIngredient(nextId(), "Mleczko (proszek)", 16.5f, 3.8f, 55.3f, 13.7f, 0.65f, 0f, 461f, IngredientCategory.OTHERS.value),
            FbIngredient(nextId(), "Mleczko (płyn)", 2.48f, .56f, 8.3f, 2.1f, 0.1f, 0f, 69f, IngredientCategory.OTHERS.value)
    )
}