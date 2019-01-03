package pl.kpob.dietdiary.di.modules

import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.eventbus.DietDiaryEventBus
import pl.kpob.dietdiary.sharedcode.eventbus.MealsUpdateEventReceiver
import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.presenter.MainPresenter
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.utils.AppSyncState
import pl.kpob.dietdiary.sharedcode.view.AppNavigator
import pl.kpob.dietdiary.sharedcode.view.popup.PopupDisplayer
import javax.inject.Named


@Module
class MainModule {

    @Provides
    fun provideMainPresenter(@Named(DataModule.NAME_DEFAULT_MEALS_REPO) mealsRepo: Repository<MealDTO, Meal>,
                             remoteDatabase: RemoteDatabase, appNavigator: AppNavigator, eventBus: DietDiaryEventBus,
                             appSyncState: AppSyncState, popupDisplayer: PopupDisplayer): MainPresenter {
        return MainPresenter(mealsRepo, remoteDatabase, appNavigator, eventBus, MealsUpdateEventReceiver(), appSyncState, popupDisplayer)
    }

}