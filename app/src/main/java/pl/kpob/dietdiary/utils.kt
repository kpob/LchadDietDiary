@file:Suppress("UNCHECKED_CAST")

package pl.kpob.dietdiary

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wealthfront.magellan.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebase.FbMeal
import java.util.*

/**
 * Created by kpob on 21.10.2017.
 */

/**
 * ANKO REPLACEMENTS
 * Drop-in replacements for the removed Anko library.
 */

/** Replaces org.jetbrains.anko.AnkoLogger */
interface AnkoLogger {
    val logTag: String get() = this::class.java.simpleName ?: "AnkoLogger"
    fun info(msg: () -> Any?) { Log.i(logTag, msg()?.toString() ?: "null") }
}

/** Replaces anko.find<T>(id) — non-nullable view lookup */
fun <T : View> View.find(@IdRes id: Int): T =
    checkNotNull(findViewById(id)) { "View with id $id not found" }

/** Replaces anko.findOptional<T>(id) — nullable view lookup */
fun <T : View> View.findOptional(@IdRes id: Int): T? = findViewById(id)

/** Replaces anko.sdk25.listeners.onClick */
inline fun View.onClick(crossinline f: () -> Unit) = setOnClickListener { f() }

/** Replaces anko.dip() on a View */
fun View.dip(n: Int): Int = (n * context.resources.displayMetrics.density).toInt()

/** Replaces Int.opaque from Anko — sets alpha to 0xFF */
val Int.opaque: Int get() = this or 0xFF000000.toInt()

/** Replaces Context.toast() from Anko */
fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

/** Replaces Context.toast(resId) from Anko */
fun Context.toast(@StringRes id: Int) =
    Toast.makeText(this, id, Toast.LENGTH_SHORT).show()

/** Replaces Context.layoutInflater from Anko */
val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)

/** Replaces ViewGroup.forEachChild from Anko */
fun ViewGroup.forEachChild(action: (View) -> Unit) {
    for (i in 0 until childCount) action(getChildAt(i))
}

/** Replaces ViewGroup.firstChild { predicate } from Anko */
fun ViewGroup.firstChild(predicate: (View) -> Boolean): View {
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        if (predicate(child)) return child
    }
    throw NoSuchElementException("No child matching predicate found")
}

/** Replaces anko.attempt { } */
inline fun attempt(f: () -> Unit) {
    try { f() } catch (_: Exception) {}
}

/** Replaces AnkoInternals.noGetter() */
fun noGetter(): Nothing =
    throw UnsupportedOperationException("Property does not have a getter")


/**
 * VIEW UTILITIES
 */
fun <T: View> ViewGroup.lastChild() : T  = getChildAt(childCount - 1) as T

inline fun<T: View> ViewGroup.forEachTypedChild(action: (T) -> Unit) {
    for (i in 0 until childCount) {
        action(getChildAt(i) as T)
    }
}

inline fun<T: View> ViewGroup.forEachTypedIndexedChild(action: (Int, T) -> Unit) {
    (0 until childCount).forEachIndexed { idx, item -> action(idx, getChildAt(item) as T) }
}

inline fun<T: View, V> ViewGroup.mapTypedChild(action: (T) -> V): List<V> =
        (0 until childCount).map { action(getChildAt(it) as T) }

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.makeInvisible() { visibility = View.INVISIBLE }

inline fun supportsOreo(action: () -> Unit) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        action.invoke()
    }
}

inline fun supportsNougat(action: () -> Unit) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        action.invoke()
    }
}


/**
 * MISC
 */
fun nextId(): String = UUID.randomUUID().toString()
fun currentTime(): Long = System.currentTimeMillis()
val Long.asReadableString: String get() =
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault()).let {
        val h = it.hour
        val m = it.minute
        "$h:${if(m < 10) "0$m" else m.toString()}"
    }


/**
 * DB ASYNC — runs block on IO thread, invokes callback on main thread
 */
fun dbAsync(block: () -> Unit, callback: () -> Unit = {}) {
    App.appScope.launch(Dispatchers.IO) {
        block()
        withContext(Dispatchers.Main) { callback() }
    }
}


/**
 * SCREEN UTILITIES
 */
fun Screen<*>.toast(msg: String) = getView()?.context?.toast(msg)
fun Screen<*>.toast(resId: Int) = getView()?.context?.toast(resId)
inline fun Screen<*>.delayed(delay: Long = 1000L, crossinline f: () -> Unit) {
    getView().postDelayed({ f() }, delay)
}


val firebaseDb get() = FirebaseDatabase.getInstance().reference

fun DatabaseReference.addIngredient(i: FbIngredient, update: Boolean = false) {
    ingredientsRef.let {
        if(update) {
            it.updateChildren(mapOf(i.id to i))
        } else {
            it.child(i.id).setValue(i)
        }
    }
}
fun DatabaseReference.increaseIngredientUsage(id: String, value: Int) {
    ingredientsRef.child(id).child("useCount").setValue(value)
}

val DatabaseReference.ingredientsRef: DatabaseReference get() = child("ingredients")
val DatabaseReference.mealsRef: DatabaseReference get() = child("meals")
val DatabaseReference.usersRef: DatabaseReference get() = child("users")
val DatabaseReference.tagsRef: DatabaseReference get() = child("tags")
val DatabaseReference.testRef: DatabaseReference get() = child("fb_test")


fun DatabaseReference.addMeal(m: FbMeal, update: Boolean = false) {
    mealsRef.let {
        if(update) {
            it.updateChildren(mapOf(m.id to m))
        } else {
            it.child(m.id).setValue(m)
        }
    }
}
fun DatabaseReference.addToken(token: String) {
    usersRef.child(token).setValue(true)
}
