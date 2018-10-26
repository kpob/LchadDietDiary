package pl.kpob.dietdiary

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.support.SingleActivity
import io.realm.Realm
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.toast
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebase.FbMeal
import pl.kpob.dietdiary.firebase.valueEventListener
import pl.kpob.dietdiary.repo.*
import pl.kpob.dietdiary.screens.AddMealScreen
import pl.kpob.dietdiary.screens.MainScreen
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : SingleActivity(), AnkoLogger {

    companion object {
        const val EXTRA_MEAL = "EXTRA_MEAL"
    }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mealRepo = MealRepository()
    private val ingredientRepo = IngredientRepository()

    private lateinit var realm: Realm

    private val ingredientsListener = valueEventListener {

        dataChanged {
            val ingredients = it?.children?.map { it.getValue(FbIngredient::class.java)!! } ?: return@dataChanged
            val ingredientsToDelete = ingredients.filter { it.deleted }
            val ingredientsToSave = ingredients.subtract(ingredientsToDelete).map { it.toRealm() }
            val idsToDelete = ingredientsToDelete.map { it.id }.toTypedArray()

            usingRealm {
                it.executeTransactionAsync {
                    ingredientRepo.insert(ingredientsToSave, RealmAddTransaction(it))
                    val spec = IngredientsByIdsSpecification(it, idsToDelete)
                    ingredientRepo.delete(spec, RealmRemoveTransaction())
                }
            }
        }
    }

    private val mealsListener = valueEventListener {

        dataChanged {
            val meals = it?.children?.map { it.getValue(FbMeal::class.java)!! } ?: return@dataChanged
            val mealsToDelete = meals.filter { it.deleted }
            val mealsToSave = meals.subtract(mealsToDelete).map { it.toRealm() }
            val idsToDelete = mealsToDelete.map { it.id }.toTypedArray()

            usingRealm {
                it.executeTransactionAsync {
                    mealRepo.insert(mealsToSave, RealmAddTransaction(it))
                    if(idsToDelete.isNotEmpty()) {
                        mealRepo.delete(MealsByIdsSpecification(it, idsToDelete), RealmRemoveTransaction())
                    }
                }
            }
        }

    }

    override fun createNavigator(): Navigator = Navigator.withRoot(MainScreen()).build()

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToken()

        val credentials = credentials
        auth
                .signInWithEmailAndPassword(credentials.login, credentials.password)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        info { it.result }
                        firebaseDb.ingredientsRef.addValueEventListener(ingredientsListener)
                        firebaseDb.mealsRef.orderByChild("time").limitToLast(200).addValueEventListener(mealsListener)
                    } else {
                        info { "ex ${it.exception}" }
                        toast("Nie można się zalogować")
                    }
                }

        realm = Realm.getDefaultInstance().apply {
            addChangeListener {
                updateView()
            }
        }
        firebaseDb.ingredientsRef.addValueEventListener(ingredientsListener)
        firebaseDb.mealsRef.orderByChild("time").limitToLast(200).addValueEventListener(mealsListener)
        
        supportsOreo {
            val channelId = "Default"
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val sound = Uri.parse("android.resource://$packageName/${R.raw.mniam}")
                val channel = NotificationChannel(channelId, "Posiłki", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Powiadomienia o posiłkach"
                    setSound(sound, AudioAttributes.Builder().setFlags(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent ?: return)
    }

    override fun onDestroy() {
        firebaseDb.ingredientsRef.removeEventListener(ingredientsListener)
        firebaseDb.mealsRef.removeEventListener(mealsListener)
        auth.signOut()
        realm.let {
            it.removeAllChangeListeners()
            it.close()
        }
        super.onDestroy()
    }

    private fun updateView() {
        val screen = getNavigator().currentScreen()
        (screen as? MainScreen)?.updateData()
    }

    private fun handleIntent(intent: Intent) {
        if(intent.hasExtra(EXTRA_MEAL)) {
            val mealType = MealType.fromString(intent.getStringExtra(EXTRA_MEAL))
            info { "EXTRA ${intent.getStringExtra(EXTRA_MEAL)}"  }
            info { "TYPE $mealType"  }
            getNavigator().goTo(AddMealScreen(mealType))
        }
    }

    private fun initToken() {
        if(AppPrefs.token.isEmpty()) {
            val token = FirebaseInstanceId.getInstance().token ?: return
            firebaseDb.addToken(token)
            AppPrefs.token = token
        }

    }

    private val credentials: Credentials get() {
        val file = assets.open("credentials")
        return BufferedReader(InputStreamReader(file, "UTF-8")).use {
            it.readLine().split(",").let { Credentials(it[0], it[1]) }
        }
    }

    data class Credentials(val login: String, val password: String)
}
