@file:Suppress("UNCHECKED_CAST")

package pl.kpob.dietdiary

import android.os.Build
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wealthfront.magellan.Screen
import io.realm.Realm
import org.jetbrains.anko.attempt
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.FbMeal
import java.util.*

/**
 * Created by kpob on 21.10.2017.
 */

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

val EditText.floatValue: Float get() = attempt { this.text.toString().toFloat() }.value ?: .0f

val Editable?.asFloatValue: Float get() = attempt { (this ?: "").toString().toFloat() }.let {
    it.value ?: 0f
}

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
 * REALM
 */
inline fun <T> usingRealm(crossinline f: (Realm) -> T) = Realm.getDefaultInstance().use {
    f(it)
}

inline fun <T> realmAsyncTransaction(crossinline transaction: (Realm) -> T, crossinline callback: () -> Unit) {
    val realm = Realm.getDefaultInstance()
    realm.executeTransactionAsync(
            { transaction(it)}, { callback(); realm.close()} , { callback(); realm.close() }
    )
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
//    testRef.child(m.id).setValue(m)
}
fun DatabaseReference.addToken(token: String) {
    usersRef.child(token).setValue(true)
}