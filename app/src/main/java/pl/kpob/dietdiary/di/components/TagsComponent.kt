package pl.kpob.dietdiary.di.components

import dagger.Component
import pl.kpob.dietdiary.di.ActivityScope
import pl.kpob.dietdiary.di.modules.TagsModule
import pl.kpob.dietdiary.di.modules.common.PopupModule
import pl.kpob.dietdiary.screens.TagCloudScreen

@ActivityScope
@Component(
        dependencies = [AppComponent::class],
        modules = [TagsModule::class, PopupModule::class])
interface TagsComponent {

    fun inject(screen: TagCloudScreen)
}