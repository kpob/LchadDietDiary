package pl.kpob.dietdiary.di.modules.common

import dagger.Module
import dagger.Provides
import io.realm.Realm
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import pl.kpob.dietdiary.db.RealmDatabase
import pl.kpob.dietdiary.db.RealmIngredient
import pl.kpob.dietdiary.db.RealmMeal
import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.repository.AllIngredientsSpecification
import pl.kpob.dietdiary.sharedcode.repository.Database
import pl.kpob.dietdiary.sharedcode.repository.IngredientsByMealTypeSpecification
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.repository.mapper.IngredientMapper
import pl.kpob.dietdiary.sharedcode.repository.mapper.MealDetailsMapper
import pl.kpob.dietdiary.sharedcode.repository.mapper.MealMapper
import javax.inject.Named


@Module
class DataModule(private val realm: Realm): AnkoLogger {

    companion object {
        const val NAME_DEFAULT_MEALS_REPO = "default"
        const val NAME_TYPED_MEALS_REPO = "byMealType"

        const val NAME_DEFAULT_MEAL_DETAILS_REPO = "md_default"
        const val NAME_TYPED_MEAL_DETAILS_REPO = "md_byMealType"
    }

    @Provides
    @Named(NAME_DEFAULT_MEALS_REPO)
    fun provideMealsRepository(@Named("allIngredients") ingredients: List<Ingredient>, db: Database<MealDTO>): Repository<MealDTO, Meal> {
        info { "ingredients default ${ingredients.size})" }
        return Repository(MealMapper(ingredients), db)
    }

    @Provides
    @Named(NAME_TYPED_MEALS_REPO)
    fun provideMealsRepositoryForMealType(@Named("typedIngredients") ingredients: List<Ingredient>, db: Database<MealDTO>): Repository<MealDTO, Meal> {
        info { "ingredients by meal type ${ingredients.size})" }
        return Repository(MealMapper(ingredients), db)
    }

    @Provides
    @Named(NAME_DEFAULT_MEAL_DETAILS_REPO)
    fun provideMealDetialsRepository(@Named("allIngredients") ingredients: List<Ingredient>, db: Database<MealDTO>): Repository<MealDTO, MealDetails> {
        info { "ingredients default ${ingredients.size})" }
        return Repository(MealDetailsMapper(ingredients), db)
    }

    @Provides
    @Named(NAME_TYPED_MEAL_DETAILS_REPO)
    fun provideMealDetialsRepositoryForMealType(@Named("typedIngredients") ingredients: List<Ingredient>, db: Database<MealDTO>): Repository<MealDTO, MealDetails> {
        info { "ingredients by meal type ${ingredients.size})" }
        return Repository(MealDetailsMapper(ingredients), db)
    }

    @Provides
    fun provideMealsDb(): Database<MealDTO> {
        return RealmDatabase(realm, RealmMeal::class.java) as Database<MealDTO>
    }

    @Provides
    fun provideIngredientsRepo(): Repository<IngredientDTO, Ingredient> {
        return Repository(IngredientMapper(), RealmDatabase(realm, RealmIngredient::class.java) as Database<IngredientDTO>)
    }

    @Provides
    @Named("allIngredients")
    fun provideIngredients(repo: Repository<IngredientDTO, Ingredient> ): List<Ingredient> {
        return repo.query(AllIngredientsSpecification())
    }

    @Provides
    @Named("typedIngredients")
    fun provideTypedIngredients(repo: Repository<IngredientDTO, Ingredient>, mealType: MealType): List<Ingredient> {
        info { "typedIngredients $mealType" }
        return repo.query(IngredientsByMealTypeSpecification(mealType))
    }

    @Provides
    fun provideRealm(): Realm = realm
}