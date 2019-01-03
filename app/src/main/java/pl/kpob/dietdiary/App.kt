package pl.kpob.dietdiary

import android.annotation.SuppressLint
import android.support.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import io.realm.RealmConfiguration
import net.danlew.android.joda.JodaTimeAndroid
import pl.kpob.dietdiary.di.components.AppComponent
import pl.kpob.dietdiary.di.modules.AppModule
import pl.kpob.dietdiary.di.components.DaggerAppComponent


/**
 * Created by kpob on 20.10.2017.
 */
class App: MultiDexApplication() {

    companion object {
        val isSyncing get() = mealsSyncing || ingredientsSyncing

        var mealsSyncing = true
        var ingredientsSyncing = true

        var appComponent: AppComponent? = null
    }

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        AppPrefs.init(this)
        JodaTimeAndroid.init(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val config = RealmConfiguration.Builder()
                .schemaVersion(1)
                .build()
        Realm.setDefaultConfiguration(config)

        appComponent = DaggerAppComponent.builder().appModule(AppModule()).build()
//        supportsNougat {
//            val shortcutManager = shortcutManager
//
//            if(shortcutManager.dynamicShortcuts.isEmpty()) {
//                val paint = Paint().apply {
//                    colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this@App, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
//                }
//
//                val shortcuts = MealType.values().map {
//                    val bmp = BitmapFactory.decodeResource(resources, it.icon.intValue).copy(Bitmap.Config.ARGB_8888, true).apply {
//                        val canvas = Canvas(this)
//                        canvas.drawBitmap(this, 0f, 0f, paint)
//                    }
//
//                    ShortcutInfo.Builder(this, it.string)
//                            .setShortLabel(it.string)
//                            .setLongLabel("Dodaj ${it.string.toLowerCase()}")
//                            .setIcon(Icon.createWithBitmap(bmp))
//                            .setIntent(Intent(this, MainActivity::class.java).apply {
//                                action = Intent.ACTION_VIEW
//                                putExtra(MainActivity.EXTRA_MEAL, it.string)
//                            })
//                            .build()
//                }
//
//                shortcutManager.addDynamicShortcuts(shortcuts)
//            }
//        }

    }
}