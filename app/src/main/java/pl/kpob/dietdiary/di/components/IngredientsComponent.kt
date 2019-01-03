package pl.kpob.dietdiary.di.components

import dagger.Component
import pl.kpob.dietdiary.di.ActivityScope
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.IngredientsModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.screens.IngredientsListScreen


@ActivityScope
@Component(
        dependencies = [AppComponent::class],
        modules = [IngredientsModule::class, NavigatorModule::class, DataModule::class])
interface IngredientsComponent {

    fun inject(screen: IngredientsListScreen)
}