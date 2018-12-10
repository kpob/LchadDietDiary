package pl.kpob.dietdiary.screens

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.ScopedScreen
import pl.kpob.dietdiary.domain.MealDetails
import pl.kpob.dietdiary.domain.MealIngredient
import pl.kpob.dietdiary.repo.MealDetailsRepository
import pl.kpob.dietdiary.repo.MealsByIdsSpecification
import pl.kpob.dietdiary.usingRealm
import pl.kpob.dietdiary.views.PieChartView

/**
 * Created by kpob on 21.10.2017.
 */
class PieChartScreen() : ScopedScreen<PieChartView>(), AnkoLogger {

    private lateinit var ids: List<String>

    constructor(ids: List<String>) : this() {
        this.ids = ids
    }

    constructor(id: String) : this() {
        this.ids = listOf(id)
    }

    private val meals: List<MealDetails> by lazy {
        usingRealm {
            val repo = MealDetailsRepository()
            val spec = MealsByIdsSpecification(it, ids.toTypedArray())
            repo.query(spec)
        }
    }

    val nutrients: Map<String, Float> by lazy {
        mapOf(
                "Węglowodany" to meals.nutrientSum { it.carbohydrates },
                "Mct" to meals.nutrientSum { it.mtc },
                "Lct" to meals.nutrientSum { it.lct },
                "Białko" to meals.nutrientSum { it.protein },
                "Błonnik" to meals.nutrientSum { it.roughage },
                "Sól" to meals.nutrientSum { it.salt }
        )
    }

    private val flattenIngredients: List<MealIngredient> by lazy {
        meals.map { it.ingredients }
                .flatten()
                .groupBy { it.id }
                .map {
                    val totalWeight = it.value.map { it.weight }.sum()
                    MealIngredient(it.value[0].id, it.value[0].name, it.value[0].calories, totalWeight)
                }
                .sortedByDescending { it.weight }
    }

    override fun createView(context: Context?) = PieChartView(context!!)

    override fun onShow(context: Context?) {
        super.onShow(context)
        launch {
            val (title, data) = when(meals.size) {
                1 -> "${meals[0].time} ${meals[0].type.string}" to meals[0].ingredients
                else -> meals[0].date to flattenIngredients
            }

            withContext(uiContext) {
                initView(title, data)
            }
        }
    }

    private fun initView(title: String, data: List<MealIngredient>) = with(view) {
        enableHomeAsUp { navigator.goBack() }
        toolbarTitle = title
        setupView(data)
    }

    private inline fun List<MealDetails>.nutrientSum(f: (MealDetails) -> Float): Float = map { f(it) }.sum()
}