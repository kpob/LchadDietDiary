package pl.kpob.dietdiary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.support.SingleActivity
import io.realm.Realm
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.di.components.DaggerActivityComponent
import pl.kpob.dietdiary.di.modules.ActivityModule
import pl.kpob.dietdiary.screens.AddMealScreen
import pl.kpob.dietdiary.screens.MainScreen
import pl.kpob.dietdiary.sharedcode.AppInitializer
import pl.kpob.dietdiary.sharedcode.model.MealType
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainActivity : SingleActivity(), CoroutineScope, AnkoLogger {

    private lateinit var parentJob: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    @Inject lateinit var initializer: AppInitializer
    lateinit var realm: Realm

    override fun createNavigator(): Navigator = Navigator.withRoot(MainScreen()).build()

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentJob = Job()
        setContentView(R.layout.activity_main)

        DaggerActivityComponent.builder()
                .appComponent(App.appComponent)
                .activityModule(ActivityModule(this))
                .build().inject(this)

        launch {
            initializer.init()
        }

        realm = Realm.getDefaultInstance().also {
            it.addChangeListener { updateView() }
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

    private fun updateView() {
        val screen = getNavigator().currentScreen()
        (screen as? MainScreen)?.updateData()
    }
    companion object {
        const val EXTRA_MEAL = "EXTRA_MEAL"
    }
}
