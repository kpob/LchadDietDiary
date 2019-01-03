package pl.kpob.dietdiary.sharedcode

import pl.kpob.dietdiary.sharedcode.utils.CredentialsProvider
import pl.kpob.dietdiary.sharedcode.utils.PlatformInitializer
import pl.kpob.dietdiary.sharedcode.utils.UserManager
import pl.kpob.dietdiary.sharedcode.utils.UserTokenProvider

class AppInitializer(
        private val userTokenProvider: UserTokenProvider,
        private val userManager: UserManager,
        private val credentialsProvider: CredentialsProvider,
        private val platformInitializer: PlatformInitializer
) {

    fun init() {
        userTokenProvider.initToken()

        userManager.signIn(credentialsProvider) {
            when(it) {
                true -> platformInitializer.refreshData()
                false -> platformInitializer.handleError()
            }
        }

        platformInitializer.initNotifications()
    }

    fun destroy() {
        platformInitializer.stopRefreshingData()
        userManager.signOut()
    }
}