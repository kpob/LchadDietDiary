package pl.kpob.dietdiary.di.components

import dagger.Component
import pl.kpob.dietdiary.di.ActivityScope
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.MainModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.di.modules.common.PopupModule
import pl.kpob.dietdiary.screens.MainScreen

@ActivityScope
@Component(
        dependencies = [AppComponent::class],
        modules = [MainModule::class, NavigatorModule::class, DataModule::class, PopupModule::class])
interface MainComponent {

    fun inject(screen: MainScreen)
}