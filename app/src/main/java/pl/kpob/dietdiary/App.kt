package pl.kpob.dietdiary

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.*
import android.graphics.drawable.Icon
import android.support.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import io.realm.FieldAttribute
import io.realm.Realm
import io.realm.RealmConfiguration
import net.danlew.android.joda.JodaTimeAndroid
import org.jetbrains.anko.shortcutManager
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.repo.IngredientContract
import pl.kpob.dietdiary.repo.TagContract
import android.support.v4.content.ContextCompat
import pl.kpob.dietdiary.repo.MealTemplateContract


/**
 * Created by kpob on 20.10.2017.
 */
class App: MultiDexApplication() {

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        AppPrefs.init(this)
        JodaTimeAndroid.init(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val config = RealmConfiguration.Builder()
                .schemaVersion(3)
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
                        ver++
                    }

                    if(ver == 2L) {
                        val realmString = realm.schema
                                .create("RealmString")
                                .addField("string", String::class.java, FieldAttribute.REQUIRED)

                        realm.schema
                                .create(MealTemplateContract.TABLE_NAME)
                                ?.addField(MealTemplateContract.ID, String::class.java, FieldAttribute.REQUIRED, FieldAttribute.PRIMARY_KEY)
                                ?.addField(MealTemplateContract.NAME, String::class.java, FieldAttribute.REQUIRED)
                                ?.addField(MealTemplateContract.TYPE, String::class.java, FieldAttribute.REQUIRED)
                                ?.addRealmListField(MealTemplateContract.INGREDIENT_IDS, realmString)
                    }
                }
                .build()
        Realm.setDefaultConfiguration(config)

        supportsNougat {
            val shortcutManager = shortcutManager

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
                            .setLongLabel("Dodaj ${it.string.toLowerCase()}")
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