package pl.kpob.dietdiary.di.components

import dagger.Component
import pl.kpob.dietdiary.di.ActivityScope
import pl.kpob.dietdiary.di.modules.CalendarModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.screens.CalendarScreen


@ActivityScope
@Component(
        dependencies = [AppComponent::class],
        modules = [CalendarModule::class, NavigatorModule::class])
interface CalendarComponent {

    fun inject(screen: CalendarScreen)
}