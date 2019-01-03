package pl.kpob.dietdiary.sharedcode.presenter

import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.model.MealDetails
import pl.kpob.dietdiary.sharedcode.model.MealIngredient
import pl.kpob.dietdiary.sharedcode.repository.MealsByIdsSpecification
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.view.ChartView
import pl.kpob.dietdiary.sharedcode.viewmodel.SummaryTabsViewModel

class ChartPresenter(
        private val ids: List<String>,
        private val mealDetailsRepository: Repository<MealDTO, MealDetails>
) {

    var view: ChartView? = null

    private val meals: List<MealDetails> by lazy {
        val spec = MealsByIdsSpecification(ids.toTypedArray())
        mealDetailsRepository.query(spec)
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

    fun onShow(view: ChartView) {
        this.view = view
        val (title, data) = when(meals.size) {
            1 -> "${meals[0].time} ${meals[0].type.string}" to meals[0].ingredients
            else -> meals[0].date to flattenIngredients
        }

        initView(title, data)
    }

    private fun initView(title: String, data: List<MealIngredient>)  {
        view?.viewTitle = title
        view?.setupView(SummaryTabsViewModel(data, meals))
    }

}