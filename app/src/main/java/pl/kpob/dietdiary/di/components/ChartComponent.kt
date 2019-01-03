package pl.kpob.dietdiary.di.components

import dagger.Component
import pl.kpob.dietdiary.di.ActivityScope
import pl.kpob.dietdiary.di.modules.ChartModule
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.screens.PieChartScreen

@ActivityScope
@Component(
        dependencies = [AppComponent::class],
        modules = [ChartModule::class, DataModule::class])
interface ChartComponent {

    fun inject(screen: PieChartScreen)
}