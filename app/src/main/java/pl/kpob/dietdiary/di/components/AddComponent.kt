package pl.kpob.dietdiary.di.components

import dagger.Component
import pl.kpob.dietdiary.di.ActivityScope
import pl.kpob.dietdiary.di.modules.AddMealModule
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.di.modules.common.PopupModule
import pl.kpob.dietdiary.screens.AddMealScreen

@ActivityScope
@Component(
        dependencies = [AppComponent::class],
        modules = [AddMealModule::class, NavigatorModule::class, DataModule::class, PopupModule::class])
interface AddComponent {

    fun inject(screen: AddMealScreen)
}