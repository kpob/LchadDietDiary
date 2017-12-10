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

class MainActivity : SingleActivity(), AnkoLogger {

    private val fbSaver: FirebaseSaver by lazy { FirebaseSaver() }

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

        firebaseDb.ingredientsRef.addValueEventListener(ingredientsListener)
        firebaseDb.mealsRef.addValueEventListener(mealsListener)
    }

    private fun addPredefinedData() {
        val data = PredefinedDataProvider.data
        DataSaver.saveIngredients(data)
        fbSaver.saveIngredients(data)
    }

    override fun onDestroy() {
        firebaseDb.ingredientsRef.removeEventListener(ingredientsListener)
        firebaseDb.mealsRef.removeEventListener(mealsListener)
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

}
