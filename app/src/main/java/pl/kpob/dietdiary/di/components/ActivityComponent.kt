package pl.kpob.dietdiary.di.components

import android.content.Context
import dagger.Component
import pl.kpob.dietdiary.MainActivity
import pl.kpob.dietdiary.di.ActivityScope
import pl.kpob.dietdiary.di.modules.ActivityModule
import pl.kpob.dietdiary.di.modules.common.DataModule

@ActivityScope
@Component(
        dependencies = [AppComponent::class],
        modules = [ActivityModule::class, DataModule::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)

    fun ctx(): Context
}