package pl.kpob.dietdiary.screens.utils

import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.domain.MealIngredient
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.firebase.FbMeal
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.realmAsyncTransaction
import pl.kpob.dietdiary.repo.*

/**
 * Created by kpob on 16.03.2018.
 */
interface MealDataInteractor {
    val possibleIngredients: List<Ingredient>
    val ingredients: List<MealIngredient>

    fun saveMeal(meal: FbMeal, counters: List<Pair<String, Int>>, cb: () -> Unit)
}

internal class IMealDataInteractor(private val type: MealType, private val meal: Meal?): MealDataInteractor {

    private val fbSaver by lazy { FirebaseSaver() }

    private val ingredientRepo by lazy { IngredientRepository() }
    private val mealRepo by lazy { MealDetailsRepository() }

    override val possibleIngredients: List<Ingredient> by lazy {
        ingredientRepo.withRealmQuery { IngredientsByMealTypeSpecification(it, type) }
    }

    override val ingredients by lazy {
        mealRepo.withRealmSingleQuery { MealByIdSpecification(it, meal!!.id) }?.ingredients ?: listOf()
    }

    override fun saveMeal(meal: FbMeal, counters: List<Pair<String, Int>>, cb: () -> Unit) {
        fbSaver.saveMeal(meal, this.meal != null)
        counters.forEach { fbSaver.updateUsageCounter(it.first, it.second) }

        realmAsyncTransaction(
                transaction = { mealRepo.insert(meal.toRealm(), RealmAddTransaction(it)) },
                callback = cb
        )
    }
}