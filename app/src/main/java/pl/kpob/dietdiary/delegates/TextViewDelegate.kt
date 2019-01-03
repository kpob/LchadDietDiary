package pl.kpob.dietdiary.delegates

import android.widget.TextView
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.find
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class TextViewDelegate(private val viewId: Int): ReadWriteProperty<BaseScreenView<*>, String> {


    override fun getValue(thisRef: BaseScreenView<*>, property: KProperty<*>): String {
        return thisRef.find<TextView>(viewId).text.toString()
    }

    override fun setValue(thisRef: BaseScreenView<*>, property: KProperty<*>, value: String) {
        thisRef.find<TextView>(viewId).text = value
    }
}