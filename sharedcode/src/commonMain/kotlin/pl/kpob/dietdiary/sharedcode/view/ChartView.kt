package pl.kpob.dietdiary.sharedcode.view

import pl.kpob.dietdiary.sharedcode.viewmodel.SummaryTabsViewModel

interface ChartView {
    fun setupView(viewModel: SummaryTabsViewModel)
    var viewTitle: String
}