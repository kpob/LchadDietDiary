package pl.kpob.dietdiary.di.components

import dagger.Component
import pl.kpob.dietdiary.di.ActivityScope
import pl.kpob.dietdiary.di.modules.AddIngredientModule
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.screens.AddIngredientScreen

@ActivityScope
@Component(
        dependencies = [AppComponent::class],
        modules = [AddIngredientModule::class, NavigatorModule::class, DataModule::class])
interface AddIngredientComponent {

    fun inject(screen: AddIngredientScreen)
}