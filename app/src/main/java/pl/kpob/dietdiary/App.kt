package pl.kpob.dietdiary

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import io.realm.RealmConfiguration
import net.danlew.android.joda.JodaTimeAndroid


/**
 * Created by kpob on 20.10.2017.
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        AppPrefs.init(this)
        JodaTimeAndroid.init(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val config = RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration { realm, oldVersion, _ ->
                    var ver = oldVersion

                    if(ver == 0L) {
                        realm.schema.get("IngredientDTO")?.addField("useCount", Int::class.java)
                        ver++
                    }
                }
                .build()
        Realm.setDefaultConfiguration(config)

    }
}