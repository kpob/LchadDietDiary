@file:Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")

package pl.kpob.dietdiary

import android.content.Context
import android.view.ViewGroup

import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class ScopedScreen<V> : CoroutineScope, Screen<V>() where V : ViewGroup, V : ScreenView<*> {

    private lateinit var parentJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onShow(context: Context?) {
        super.onShow(context)
        parentJob = Job()
    }

    override fun onHide(context: Context?) {
        parentJob.cancel()
        super.onHide(context)
    }

}
