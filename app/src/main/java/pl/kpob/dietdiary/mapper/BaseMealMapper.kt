package pl.kpob.dietdiary.mapper

import io.realm.Realm
import org.joda.time.DateTime
import pl.kpob.dietdiary.MealType
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.db.MealDTO

/**
 * Created by kpob on 11.12.2017.
 */
abstract class BaseMealMapper {

    protected fun amountOfNutrient(mealIngredients: List<Pair<IngredientDTO, Float>>, nutrient: (IngredientDTO) -> Float) : Float =
            mealIngredients.map { (i, w) -> nutrient(i) * w * 0.01f}.sum()

    protected fun calculateCalories(mealIngredients: List<Pair<IngredientDTO, Float>>): Float =
            mealIngredients.map { (i, weight) -> i.calories * weight / 100f }.sum()

    protected fun calculateLct(mealIngredients: List<Pair<IngredientDTO, Float>>): Float =
            mealIngredients.map { (i, weight) -> i.lct  * weight / 100f }.sum()

    protected fun Long.toDateString() =
            DateTime(this).let {
                val (h, m) = it.hourOfDay to it.minuteOfHour
                "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}"
            }

    protected fun String.toMealType() = MealType.fromString(this)

    protected fun MealDTO.toIngredientsWithWeight(): List<Pair<IngredientDTO, Float>> {
        val mealIngredientsIds = ingredients.map { it.ingredientId }
        return this@BaseMealMapper.ingredients
                .filter { mealIngredientsIds.contains(it.id) }
                .map { i -> i to ingredients.first { it.ingredientId == i.id }.weight }
    }

    protected val ingredients: MutableList<IngredientDTO> by lazy {
        usingRealm { it.copyFromRealm(it.where(IngredientDTO::class.java).findAll()) }
    }

    private inline fun <T> usingRealm(crossinline f: (Realm) -> T) = Realm.getDefaultInstance().use { f(it) }
}