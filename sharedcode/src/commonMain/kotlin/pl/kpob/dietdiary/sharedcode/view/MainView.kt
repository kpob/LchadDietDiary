package pl.kpob.dietdiary.sharedcode.view

import pl.kpob.dietdiary.sharedcode.viewmodel.MealsViewModel

interface MainView: SyncView {

    fun showMeals(viewModel: MealsViewModel)
    fun closeDrawers()

    fun hideMeals()
}