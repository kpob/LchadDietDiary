package pl.kpob.dietdiary.di.components

import dagger.Component
import pl.kpob.dietdiary.di.modules.AppModule
import pl.kpob.dietdiary.sharedcode.db.RemoteDatabase
import pl.kpob.dietdiary.sharedcode.eventbus.DietDiaryEventBus
import pl.kpob.dietdiary.sharedcode.utils.AppSyncState
import pl.kpob.dietdiary.sharedcode.utils.UserManager
import pl.kpob.dietdiary.sharedcode.utils.UserTokenProvider
import javax.inject.Singleton


@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun eventBus(): DietDiaryEventBus
    fun appSyncState(): AppSyncState
    fun remoteDb(): RemoteDatabase
    fun tokenProvider(): UserTokenProvider
    fun userManager(): UserManager
}