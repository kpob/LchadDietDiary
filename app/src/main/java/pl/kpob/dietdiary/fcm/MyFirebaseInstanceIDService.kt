package pl.kpob.dietdiary.fcm

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import pl.kpob.dietdiary.AppPrefs
import pl.kpob.dietdiary.addToken
import pl.kpob.dietdiary.firebaseDb
import pl.kpob.dietdiary.usersRef

/**
 * Created by kpob on 28.10.2017.
 */
class MyFirebaseInstanceIDService: FirebaseInstanceIdService(), AnkoLogger {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token ?: return
        info {  "Refreshed token: " + refreshedToken }

        sendRegistrationToServer(refreshedToken)
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.

     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.

     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String) {
        val oldToken = AppPrefs.token
        if(token != oldToken && oldToken.isNotEmpty()) {
            firebaseDb.usersRef.child(oldToken).setValue(false)
        }
        firebaseDb.addToken(token)
        AppPrefs.token = token
    }
}