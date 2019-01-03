package pl.kpob.dietdiary.sharedcode.view

import pl.kpob.dietdiary.sharedcode.model.MealIngredient
import pl.kpob.dietdiary.sharedcode.model.Metrics

interface RangeSummaryView: TitledView {
    fun setupView(ingredients: List<MealIngredient>, metrics: Metrics)
}