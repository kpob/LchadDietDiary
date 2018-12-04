package pl.kpob.dietdiary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.support.SingleActivity
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import pl.kpob.dietdiary.domain.MealType
import pl.kpob.dietdiary.screens.AddMealScreen
import pl.kpob.dietdiary.screens.MainScreen
import kotlin.coroutines.CoroutineContext

class MainActivity : SingleActivity(), CoroutineScope, AnkoLogger {

    private lateinit var parentJob: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    private lateinit var initializer: AppInitializer

    override fun createNavigator(): Navigator = Navigator.withRoot(MainScreen()).build()

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentJob = Job()
        setContentView(R.layout.activity_main)
        launch {
            initializer = AppInitializer(this@MainActivity)
            initializer.init()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent ?: return)
    }

    override fun onDestroy() {
        initializer.destroy()
        super.onDestroy()
    }

    private fun handleIntent(intent: Intent) {
        if(intent.hasExtra(EXTRA_MEAL)) {
            val mealType = MealType.fromString(intent.getStringExtra(EXTRA_MEAL))
            getNavigator().goTo(AddMealScreen(mealType))
        }
    }

    companion object {
        const val EXTRA_MEAL = "EXTRA_MEAL"
    }
}
