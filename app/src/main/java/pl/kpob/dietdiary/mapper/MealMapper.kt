package pl.kpob.dietdiary.mapper

import io.realm.Realm
import org.jetbrains.anko.AnkoLogger
import org.joda.time.DateTime
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.db.MealDTO

/**
 * Created by kpob on 22.10.2017.
 */
object MealMapper : AnkoLogger{

    fun map(input: List<MealDTO>): List<Meal> = input.map {
        val dt = DateTime(it.time)
        val (h, m) = dt.hourOfDay to dt.minuteOfHour
        val time = "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}"
        Meal(
                it.id,
                time,
                "${dt.dayOfMonth}-${dt.monthOfYear}-${dt.year}",
                it.name.toMealType(),
                calculateCalories(it),
                calculateLct(it),
                dt.dayOfYear,
                dt.year,
                it.time
        )
    }

    fun mapToMealDetails(input: MealDTO): MealDetails {
        val mealIngredients = input.ingredients.map { i -> ingredients.first { it.id == i.ingredientId } to i.weight }

        return MealDetails(
                time = input.time.toDateString(),
                type = input.name.toMealType(),
                caloriesTotal = calculateCalories(input),
                protein = amountOfNutrient(mealIngredients) { it.protein },
                carbohydrates = amountOfNutrient(mealIngredients) { it.carbohydrates },
                salt = amountOfNutrient(mealIngredients) { it.salt },
                lct = amountOfNutrient(mealIngredients) { it.lct },
                mtc = amountOfNutrient(mealIngredients) { it.mtc },
                roughage = amountOfNutrient(mealIngredients) { it.roughage },
                ingredients = mealIngredients.map { (i, w) -> MealIngredient(i.id, i.name, i.calories, w) }.filter { it.weight > 0 }
        )
    }

    private fun amountOfNutrient(mealIngredients: List<Pair<IngredientDTO, Float>>, nutrient: (IngredientDTO) -> Float) : Float =
        mealIngredients.map { (i, w) -> nutrient(i) * w * 0.01f}.sum()

    private fun calculateCalories(dto: MealDTO): Float =
        dto.ingredients.map { i -> (ingredients.find { it.id == i.ingredientId }?.calories ?: 0f) * i.weight / 100f }.sum()

    private fun calculateLct(dto: MealDTO): Float =
        dto.ingredients.map { i -> (ingredients.find { it.id == i.ingredientId }?.lct ?: 0f) * i.weight / 100f }.sum()


    private fun Long.toDateString() =
            DateTime(this).let {
                val (h, m) = it.hourOfDay to it.minuteOfHour
                "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}"
            }

    private fun Long.toDayOfYear() = DateTime(this).dayOfYear

    private fun String.toMealType() = MealType.fromString(this)

    private val ingredients by lazy {
        usingRealm { it.copyFromRealm(it.where(IngredientDTO::class.java).findAll()) }
    }

    private inline fun <T> usingRealm(crossinline f: (Realm) -> T) = Realm.getDefaultInstance().use {
        f(it)
    }

}