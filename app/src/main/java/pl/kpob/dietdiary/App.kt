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
import io.realm.FieldAttribute
import io.realm.Realm
import io.realm.RealmConfiguration
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.repo.IngredientContract
import pl.kpob.dietdiary.repo.TagContract




/**
 * Created by kpob on 20.10.2017.
 */
class App: MultiDexApplication() {

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        AppPrefs.init(this)
        AndroidThreeTen.init(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())

        val config = RealmConfiguration.Builder()
                .schemaVersion(2)
                .migration { realm, oldVersion, _ ->
                    var ver = oldVersion

                    if(ver == 0L) {
                        realm.schema
                                .get(IngredientContract.TABLE_NAME)
                                ?.addField(IngredientContract.USE_COUNT, Int::class.java)
                        ver++
                    }

                    if(ver == 1L) {
                        realm.schema
                                .create(TagContract.TABLE_NAME)
                                ?.addField(TagContract.ID, String::class.java, FieldAttribute.REQUIRED, FieldAttribute.PRIMARY_KEY)
                                ?.addField(TagContract.NAME, String::class.java, FieldAttribute.REQUIRED)
                                ?.addField(TagContract.ACTIVE_COLOR, Int::class.java)
                                ?.addField(TagContract.ACTIVE_TEXT_COLOR, Int::class.java)
                                ?.addField(TagContract.CREATION_TIME, Long::class.java)
                                ?.addField(TagContract.TEXT_COLOR, Int::class.java)
                                ?.addField(TagContract.COLOR, Int::class.java)
                    }
                }
                .build()
        Realm.setDefaultConfiguration(config)

        supportsNougat {
            val shortcutManager = getSystemService(ShortcutManager::class.java)!!

            if(shortcutManager.dynamicShortcuts.isEmpty()) {
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