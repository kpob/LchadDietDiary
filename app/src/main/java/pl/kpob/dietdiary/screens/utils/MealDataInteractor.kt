package pl.kpob.dietdiary.screens.utils

import pl.kpob.dietdiary.dbAsync
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.Meal
import pl.kpob.dietdiary.domain.MealIngredient
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.firebase.FbMeal
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.repo.IngredientRepository
import pl.kpob.dietdiary.repo.MealDetailsRepository
import pl.kpob.dietdiary.repo.MealRepository

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
    private val mealDetailsRepo by lazy { MealDetailsRepository() }
    private val mealRepo by lazy { MealRepository() }

    override val possibleIngredients: List<Ingredient> by lazy {
        ingredientRepo.getByCategories(type.filters.map { it.value }.toIntArray())
    }

    override val ingredients by lazy {
        mealDetailsRepo.getById(meal!!.id)?.ingredients ?: listOf()
    }

    override fun saveMeal(meal: FbMeal, counters: List<Pair<String, Int>>, cb: () -> Unit) {
        fbSaver.saveMeal(meal, this.meal != null)
        counters.forEach { fbSaver.updateUsageCounter(it.first, it.second) }

        val (mealDto, ingredientDtos) = meal.toDtoPair()
        dbAsync(
            block = { mealRepo.insertMealWithIngredients(mealDto, ingredientDtos) },
            callback = cb
        )
    }
}
