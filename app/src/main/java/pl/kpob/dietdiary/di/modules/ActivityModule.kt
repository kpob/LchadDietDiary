package pl.kpob.dietdiary.di.modules

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import pl.kpob.dietdiary.sharedcode.AppInitializer
import pl.kpob.dietdiary.sharedcode.utils.CredentialsProvider
import pl.kpob.dietdiary.sharedcode.utils.PlatformInitializer
import pl.kpob.dietdiary.sharedcode.utils.UserManager
import pl.kpob.dietdiary.sharedcode.utils.UserTokenProvider
import pl.kpob.dietdiary.utils.AndroidCredentialsProvider
import pl.kpob.dietdiary.utils.AndroidPlatformInitalizer
import pl.kpob.dietdiary.utils.AndroidUserManager

@Module
class ActivityModule(private val activity: Activity) {


    @Provides
    fun providesAppInitializer(platformInitializer: PlatformInitializer, tokenProvider: UserTokenProvider,userManager: UserManager, credentialsProvider: CredentialsProvider): AppInitializer {
        return AppInitializer(
                credentialsProvider = credentialsProvider,
                userManager = userManager,
                userTokenProvider = tokenProvider,
                platformInitializer = platformInitializer
        )
    }

    @Provides
    fun provideContext(): Context = activity

    @Provides
    fun providePlatformInitializer(): PlatformInitializer = AndroidPlatformInitalizer(activity)

    @Provides
    fun provideCredentialsProvider(): CredentialsProvider = AndroidCredentialsProvider(activity)
}