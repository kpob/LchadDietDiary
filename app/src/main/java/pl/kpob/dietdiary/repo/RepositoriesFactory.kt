package pl.kpob.dietdiary.repo

import io.realm.Realm
import pl.kpob.dietdiary.db.RealmDatabase
import pl.kpob.dietdiary.db.RealmIngredient
import pl.kpob.dietdiary.db.RealmMeal
import pl.kpob.dietdiary.db.RealmMealTemplate
import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.repository.mapper.IngredientMapper
import pl.kpob.dietdiary.sharedcode.repository.mapper.MealDetailsMapper
import pl.kpob.dietdiary.sharedcode.repository.mapper.MealMapper
import pl.kpob.dietdiary.sharedcode.repository.mapper.MealTemplateMapper

object RepositoriesFactory {

    fun mealDetailsRepository(realm: Realm, ingredients: List<Ingredient>): Repository<RealmMeal, MealDetails> {
        return Repository(
                MealDetailsMapper(ingredients),
                RealmDatabase(realm, RealmMeal::class.java)
        )
    }

    fun mealsRepository(realm: Realm, ingredients: List<Ingredient>): Repository<RealmMeal, Meal> {
        return Repository(
                MealMapper(ingredients),
                RealmDatabase(realm, RealmMeal::class.java)
        )
    }

    fun ingredientsRepository(realm: Realm): Repository<RealmIngredient, Ingredient> {
        return Repository(
                IngredientMapper(),
                RealmDatabase(realm, RealmIngredient::class.java)
        )
    }

    fun mealTemplateRepository(realm: Realm, ingredients: List<Ingredient>): Repository<RealmMealTemplate, MealTemplate> {
        return Repository(
                MealTemplateMapper(ingredients),
                RealmDatabase(realm, RealmMealTemplate::class.java)
        )
    }


}