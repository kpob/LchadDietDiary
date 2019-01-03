package pl.kpob.dietdiary.utils

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import pl.kpob.dietdiary.DataManager
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.sharedcode.utils.PlatformInitializer
import pl.kpob.dietdiary.supportsOreo
import javax.inject.Inject

class AndroidPlatformInitalizer(private val ctx: Context): PlatformInitializer {

    override fun refreshData() {
        DataManager.start()
    }

    override fun stopRefreshingData() {
        DataManager.stop()
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun initNotifications() {
        supportsOreo {
            val channelId = "Default"
            if (ctx.notificationManager.getNotificationChannel(channelId) == null) {
                val sound = Uri.parse("android.resource://${ctx.packageName}/${R.raw.mniam}")
                val channel = NotificationChannel(channelId, "Posiłki", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Powiadomienia o posiłkach"
                    setSound(sound, AudioAttributes.Builder().setFlags(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                }
                ctx.notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override fun handleError() {
        ctx.runOnUiThread { ctx.toast("Nie można się zalogować") }
    }

}