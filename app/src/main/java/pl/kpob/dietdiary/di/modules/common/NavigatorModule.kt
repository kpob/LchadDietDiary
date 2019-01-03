package pl.kpob.dietdiary.di.modules.common

import com.wealthfront.magellan.Navigator
import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.utils.AndroidAppNavigator
import pl.kpob.dietdiary.sharedcode.view.AppNavigator

@Module
class NavigatorModule(private val navigator: Navigator) {

    @Provides
    fun provideAppNavigator(): AppNavigator = AndroidAppNavigator(navigator)
}