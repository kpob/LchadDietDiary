@file:Suppress("UNCHECKED_CAST")

package pl.kpob.dietdiary

import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebase.FbMeal
import java.util.*

/**
 * Created by kpob on 21.10.2017.
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

fun nextId(): String = UUID.randomUUID().toString()
fun currentTime(): Long = System.currentTimeMillis()

inline fun <T> usingRealm(crossinline f: (Realm) -> T) = Realm.getDefaultInstance().use {
    f(it)
}


inline fun <T> realmAsyncTransaction(crossinline f: (Realm) -> T, crossinline cb: () -> Unit) = Realm.getDefaultInstance().use {
    val realm = Realm.getDefaultInstance()
    realm.executeTransactionAsync(
            { f(it)}, {cb(); realm.close()}, {cb(); realm.close()}
    )
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