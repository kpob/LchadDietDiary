package pl.kpob.dietdiary

import android.support.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import io.realm.RealmConfiguration
import net.danlew.android.joda.JodaTimeAndroid
import pl.kpob.dietdiary.repo.IngredientContract
import pl.kpob.dietdiary.repo.TagContract


/**
 * Created by kpob on 20.10.2017.
 */
class App: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        AppPrefs.init(this)
        JodaTimeAndroid.init(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

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
                                ?.addField(TagContract.ID, String::class.java)
                                ?.addField(TagContract.NAME, String::class.java)
                                ?.addField(TagContract.ACTIVE_COLOR, Int::class.java)
                                ?.addField(TagContract.ACTIVE_TEXT_COLOR, Int::class.java)
                                ?.addField(TagContract.CREATION_TIME, Long::class.java)
                                ?.addField(TagContract.TEXT_COLOR, Int::class.java)
                                ?.addField(TagContract.COLOR, Int::class.java)
                    }
                }
                .build()
        Realm.setDefaultConfiguration(config)

    }
}