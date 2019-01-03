package pl.kpob.dietdiary.di.modules

import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.sharedcode.presenter.CalendarPresenter
import pl.kpob.dietdiary.sharedcode.view.AppNavigator

@Module
class CalendarModule {

    @Provides
    fun provideCalendarModule(appNavigator: AppNavigator): CalendarPresenter {
        return CalendarPresenter(appNavigator)
    }
}