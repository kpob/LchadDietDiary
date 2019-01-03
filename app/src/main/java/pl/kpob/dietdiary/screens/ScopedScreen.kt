@file:Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")

package pl.kpob.dietdiary.screens

import android.content.Context
import android.view.ViewGroup

import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenView
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import pl.kpob.dietdiary.App
import pl.kpob.dietdiary.MainActivity
import pl.kpob.dietdiary.di.components.AppComponent
import kotlin.coroutines.CoroutineContext

abstract class ScopedScreen<V> : CoroutineScope, Screen<V>() where V : ViewGroup, V : ScreenView<*> {

    private lateinit var parentJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    val uiContext: CoroutineContext
        get() = Dispatchers.Main

    val realm: Realm get() = (activity as MainActivity).realm

    val appComponent: AppComponent? get() = App.appComponent

    override fun onShow(context: Context?) {
        super.onShow(context)
        parentJob = Job()
    }

    override fun onHide(context: Context?) {
        parentJob.cancel()
        super.onHide(context)
    }

}
