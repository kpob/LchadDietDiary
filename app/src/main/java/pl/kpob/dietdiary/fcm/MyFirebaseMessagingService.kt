package pl.kpob.dietdiary.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pl.kpob.dietdiary.AppPrefs
import pl.kpob.dietdiary.addToken
import pl.kpob.dietdiary.firebaseDb
import pl.kpob.dietdiary.usersRef


/**
 * Created by kpob on 28.10.2017.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMsgService"

    /**
     * Called when a new FCM registration token is generated (replaces FirebaseInstanceIdService).
     */
    override fun onNewToken(token: String) {
        Log.i(TAG, "Refreshed token: $token")
        val oldToken = AppPrefs.token
        if (token != oldToken && oldToken.isNotEmpty()) {
            firebaseDb.usersRef.child(oldToken).setValue(false)
        }
        firebaseDb.addToken(token)
        AppPrefs.token = token
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.i(TAG, "Message data payload: ${remoteMessage.data}")
            scheduleJob()
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.i(TAG, "Message Notification Body: ${it.body}")
        }
    }

    /**
     * Schedule a job using WorkManager or other mechanism.
     */
    private fun scheduleJob() {
        // TODO: implement using WorkManager if background processing is needed
    }
}
