package pl.kpob.dietdiary.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by kpob on 27.10.2017.
 */
class FirebaseValueEventListener : ValueEventListener, AnkoLogger {


    private var cancelled: (DatabaseError?) -> Unit = {}
    private var dataChanged: (DataSnapshot?) -> Unit = {}

    fun cancelled(f: (DatabaseError?) -> Unit) { cancelled = f }
    fun dataChanged(f: (DataSnapshot?) -> Unit) { dataChanged = f }

    override fun onCancelled(e: DatabaseError?) {
        info { "error $e" }
        cancelled(e)
    }

    override fun onDataChange(snapshot: DataSnapshot?) {
        dataChanged(snapshot)
    }
}

fun valueEventListener(init: FirebaseValueEventListener.() -> Unit) =
        FirebaseValueEventListener().apply { init() }