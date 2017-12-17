package pl.kpob.dietdiary.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import pl.kpob.dietdiary.MainActivity
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


/**
 * Created by kpob on 28.10.2017.
 */
class MyFirebaseMessagingService: FirebaseMessagingService(), AnkoLogger {

    private val TAG = "MyFirebaseMsgService"

    /**
     * Called when message is received.

     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        info {  "From: " + remoteMessage!!.from }

        // Check if message contains a data payload.
        if (remoteMessage?.data?.isNotEmpty() == true) {
            info { "Message data payload: ${remoteMessage.data}" }

                scheduleJob()
        }

        // Check if message contains a notification payload.
        if (remoteMessage?.notification != null) {
            info {"Message Notification Body: " + remoteMessage.notification.body!! }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
//        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
//        val myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService::class.java)
//                .setTag("my-job-tag")
//                .build()
//        dispatcher.schedule(myJob)
        // [END dispatch_job]
    }

}