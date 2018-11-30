package pl.kpob.dietdiary

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import org.jetbrains.anko.*
import pl.kpob.dietdiary.domain.Credentials
import pl.kpob.dietdiary.worker.RefreshIngredientsService
import pl.kpob.dietdiary.worker.RefreshMealsService
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.ref.WeakReference

class AppInitializer(activity: MainActivity) : AnkoLogger {

    private val ref: WeakReference<MainActivity> = WeakReference(activity)
    private val activity: MainActivity? get() = ref.get()

    private val firebaseAuth by lazy {  FirebaseAuth.getInstance() }

    @RequiresApi(Build.VERSION_CODES.O)
    fun init() {
        App.ingredientsSyncing = true
        App.mealsSyncing = true
        initToken()

        with(credentials) {
            firebaseAuth
                    .signInWithEmailAndPassword(login, password)
                    .addOnCompleteListener {
                        when (it.isSuccessful) {
                            true -> refreshData()
                            false -> handleError(it.exception)
                        }
                    }
        }

        createNotificationChannelIfNeeded()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelIfNeeded() {
        val activity = activity ?: return
        supportsOreo {
            val channelId = "Default"
            if (activity.notificationManager.getNotificationChannel(channelId) == null) {
                val sound = Uri.parse("android.resource://${activity.packageName}/${R.raw.mniam}")
                val channel = NotificationChannel(channelId, "Posiłki", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Powiadomienia o posiłkach"
                    setSound(sound, AudioAttributes.Builder().setFlags(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                }
                activity.notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun destroy() {
        firebaseAuth.signOut()
    }

    private val credentials: Credentials get() {
        val ctx = activity ?: return Credentials("", "")
        val file = ctx.assets.open("credentials")
        return BufferedReader(InputStreamReader(file, "UTF-8")).use {
            it.readLine().split(",").let { Credentials(it[0], it[1]) }
        }
    }

    private fun initToken() {
        if(AppPrefs.token.isEmpty()) {
            val token = FirebaseInstanceId.getInstance().token ?: return
            firebaseDb.addToken(token)
            AppPrefs.token = token
        }
    }

    private fun refreshData() {
        activity?.startService<RefreshMealsService>()
        activity?.startService<RefreshIngredientsService>()
    }

    private fun handleError(exception: Exception?) {
        info { "ex $exception" }
        activity?.toast("Nie można się zalogować")
    }


}