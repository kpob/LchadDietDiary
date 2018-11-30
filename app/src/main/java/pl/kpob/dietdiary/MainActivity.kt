package pl.kpob.dietdiary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.support.SingleActivity
import io.realm.Realm
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.screens.AddMealScreen
import pl.kpob.dietdiary.screens.MainScreen

class MainActivity : SingleActivity(), AnkoLogger {

    companion object {
        const val EXTRA_MEAL = "EXTRA_MEAL"
    }

    private lateinit var initializer: AppInitializer
    private lateinit var realm: Realm

    override fun createNavigator(): Navigator = Navigator.withRoot(MainScreen()).build()

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializer = AppInitializer(this)
        initializer.init()

        realm = Realm.getDefaultInstance().apply {
            addChangeListener {
                updateView()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent ?: return)
    }

    override fun onDestroy() {
        initializer.destroy()
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
            getNavigator().goTo(AddMealScreen(mealType))
        }
    }
}
