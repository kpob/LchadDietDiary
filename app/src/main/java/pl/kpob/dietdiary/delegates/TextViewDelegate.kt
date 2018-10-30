package pl.kpob.dietdiary.delegates

import android.widget.TextView
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.find
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class TextViewDelegate(private val viewId: Int): ReadWriteProperty<BaseScreenView<*>, CharSequence> {


    override fun getValue(thisRef: BaseScreenView<*>, property: KProperty<*>): CharSequence {
        return thisRef.find<TextView>(viewId).text
    }

    override fun setValue(thisRef: BaseScreenView<*>, property: KProperty<*>, value: CharSequence) {
        thisRef.find<TextView>(viewId).text = value
    }
}