package pl.kpob.dietdiary.sharedcode.model

import kotlinx.serialization.Serializable
import pl.kpob.dietdiary.sharedcode.utils.asFormattedString

data class Credentials(
        val login: String,
        val password: String
)

data class Meal(
        val id: String,
        val time: String,
        val date: String,
        val type: MealType,
        val calories: Float,
        val lct: Float,
        val dayOfYear: Int,
        val year: Int,
        val timestamp: Long,
        val isToday: Boolean
)

data class MealDetails(
        val time: String,
        val date: String,
        val type: MealType,
        val caloriesTotal: Float,
        val mtc: Float,
        val lct: Float,
        val carbohydrates: Float,
        val protein: Float,
        val salt: Float,
        val roughage: Float,
        val ingredients: List<MealIngredient>,
        val dayOfYear: Int,
        val year: Int
)

data class MealIngredient(
        val id: String,
        val name: String,
        val calories: Float,
        val weight: Float = 0f
) {
    val caloriesString get() = "${calories.asFormattedString()} kcal"
}

data class MealTemplate(
        val id: String,
        val name: String,
        val type: MealType,
        val ingredients: List<Ingredient>
)

@Serializable
data class Ingredient(
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
        val useCount: Int = 0
)

data class IngredientUsage(val itemId: String, val counter: Int)

data class MealPart(val ingredient: Ingredient, val weight: Float) {
    val kcal: Float = ingredient.calories * weight/100f
}


enum class MealType(val string: String, val filters: List<IngredientCategory>) {
    MILK("Mleczko", listOf(IngredientCategory.OTHERS)),
    DESSERT("Deserek", listOf(IngredientCategory.OILS, IngredientCategory.PORRIDGE, IngredientCategory.DIARY, IngredientCategory.FRUITS, IngredientCategory.FRUITS_TUBE)),
    DINNER("Obiadek", listOf(IngredientCategory.OILS, IngredientCategory.PORRIDGE, IngredientCategory.DINNERS, IngredientCategory.DIARY, IngredientCategory.FRUITS, IngredientCategory.FRUITS_TUBE)),
    OTHER("Przekąska", listOf(IngredientCategory.OTHERS));

    companion object {
        fun fromString(s: String) = values().firstOrNull { it.string == s || it.name == s} ?: OTHER
    }
}

enum class IngredientCategory(val value: Int, val label: String) {
    PORRIDGE(1, "Kaszka"),
    FRUITS(2, "Owocki"),
    DINNERS(3, "Obiadki"),
    OTHERS(4, "Inne"),
    OILS(5, "Oleje"),
    FRUITS_TUBE(6, "Owocowe tubki"),
    DIARY(7, "Nabiał");

    companion object {
        fun fromInt(i: Int) = values().firstOrNull { it.value == i } ?: PORRIDGE
        fun fromString(s: String) = values().firstOrNull { it.label == s } ?: PORRIDGE

        fun stringValues() = values().map { it.label }
    }
}



data class Metrics(val values: List<Float>) {

    val total by lazy { values.sum() }
    val max by lazy { values.max() }
    val min by lazy { valid.min() }

    val avg by lazy { valid.average() }

    private val valid: List<Float> by lazy {
        val avg = values.average()
        values.filter { it > avg * THRESHOLD }
    }
    companion object {
        private const val THRESHOLD = .8F
    }

}