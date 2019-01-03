package pl.kpob.dietdiary.di.modules

import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.utils.AndroidAppSyncState
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.eventbus.DietDiaryEventBus
import pl.kpob.dietdiary.sharedcode.utils.AppSyncState
import pl.kpob.dietdiary.sharedcode.utils.UserManager
import pl.kpob.dietdiary.sharedcode.utils.UserTokenProvider
import pl.kpob.dietdiary.utils.AndroidUserManager
import pl.kpob.dietdiary.utils.AndroidUserTokenProvider
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideAppSyncState(): AppSyncState = AndroidAppSyncState

    @Provides
    @Singleton
    fun provideEventBus(): DietDiaryEventBus = DietDiaryEventBus()

    @Provides
    @Singleton
    fun provideRemoteDb(): RemoteDatabase = FirebaseSaver()

    @Provides
    @Singleton
    fun provideUserTokenProvider(remoteDatabase: RemoteDatabase): UserTokenProvider = AndroidUserTokenProvider(remoteDatabase)

    @Provides
    @Singleton
    fun provideUserManager(): UserManager = AndroidUserManager()


}