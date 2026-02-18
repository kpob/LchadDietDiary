package pl.kpob.dietdiary

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.*
import android.graphics.drawable.Icon
import android.os.StrictMode
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pl.kpob.dietdiary.db.AppDatabase
import pl.kpob.dietdiary.domain.MealType


/**
 * Created by kpob on 20.10.2017.
 */
class App : MultiDexApplication() {

    companion object {
        lateinit var db: AppDatabase
        val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getInstance(this)
        AppPrefs.init(this)
        AndroidThreeTen.init(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())

        supportsNougat {
            val shortcutManager = getSystemService(ShortcutManager::class.java)!!

            if (shortcutManager.dynamicShortcuts.isEmpty()) {
                val paint = Paint().apply {
                    colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this@App, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
                }

                val shortcuts = MealType.values().map {
                    val bmp = BitmapFactory.decodeResource(resources, it.icon).copy(Bitmap.Config.ARGB_8888, true).apply {
                        val canvas = Canvas(this)
                        canvas.drawBitmap(this, 0f, 0f, paint)
                    }

                    ShortcutInfo.Builder(this, it.string)
                            .setShortLabel(it.string)
                            .setLongLabel("Dodaj ${it.string.lowercase()}")
                            .setIcon(Icon.createWithBitmap(bmp))
                            .setIntent(Intent(this, MainActivity::class.java).apply {
                                action = Intent.ACTION_VIEW
                                putExtra(MainActivity.EXTRA_MEAL, it.string)
                            })
                            .build()
                }

                shortcutManager.addDynamicShortcuts(shortcuts)
            }
        }
    }
}
