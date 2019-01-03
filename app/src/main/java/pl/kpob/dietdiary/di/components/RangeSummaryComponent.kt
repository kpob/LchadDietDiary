package pl.kpob.dietdiary.di.components

import dagger.Component
import pl.kpob.dietdiary.di.ActivityScope
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.RangeSummaryModule
import pl.kpob.dietdiary.screens.RangeSummaryScreen

@ActivityScope
@Component(
        dependencies = [AppComponent::class],
        modules = [RangeSummaryModule::class, DataModule::class])
interface RangeSummaryComponent {


    fun inject(screen: RangeSummaryScreen)
}