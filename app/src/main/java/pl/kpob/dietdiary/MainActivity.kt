package pl.kpob.dietdiary

import android.os.Bundle
import com.google.firebase.iid.FirebaseInstanceId
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.support.SingleActivity
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.db.DataSaver
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.firebase.valueEventListener
import pl.kpob.dietdiary.screens.MainScreen
import pl.kpob.dietdiary.server.FbIngredient
import pl.kpob.dietdiary.server.FbMeal
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : SingleActivity(), AnkoLogger {

    private val fbSaver: FirebaseSaver by lazy { FirebaseSaver() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    private val ingredientsListener = valueEventListener {

        dataChanged {
            val ingredients = it?.children?.map { it.getValue(FbIngredient::class.java)!! } ?: return@dataChanged
            if(ingredients.isEmpty()) {
                addPredefinedData()
            } else {
                DataSaver.updateIngredients(ingredients) {
                    updateView()
                }
            }
        }
    }

    private val mealsListener = valueEventListener {

        dataChanged {
            val meals = it?.children?.map { it.getValue(FbMeal::class.java)!! } ?: return@dataChanged
            DataSaver.updateMeals(meals) {
                updateView()
            }
        }

    }

    override fun createNavigator() = Navigator.withRoot(MainScreen()).build()!!

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
                        firebaseDb.mealsRef.addValueEventListener(mealsListener)
                    } else {
                        info { "ex ${it.exception}" }
                        toast("Nie można się zalogować")
                    }
                }

    }

    private fun addPredefinedData() {
        val data = PredefinedDataProvider.data
        DataSaver.saveIngredients(data)
        fbSaver.saveIngredients(data)
    }

    override fun onDestroy() {
        firebaseDb.ingredientsRef.removeEventListener(ingredientsListener)
        firebaseDb.mealsRef.removeEventListener(mealsListener)
        auth.signOut()
        super.onDestroy()
    }

    private fun updateView() {
        val screen = getNavigator().currentScreen()
        (screen as? MainScreen)?.updateData()
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
