package pl.kpob.dietdiary.delegates

import android.widget.TextView
import com.wealthfront.magellan.BaseScreenView
import org.jetbrains.anko.find
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class TextViewFloatValueDelegate(private val viewId: Int, private val prefix: String): ReadWriteProperty<BaseScreenView<*>, Float> {


    override fun getValue(thisRef: BaseScreenView<*>, property: KProperty<*>): Float {
        throw Exception()
    }

    override fun setValue(thisRef: BaseScreenView<*>, property: KProperty<*>, value: Float) {
        thisRef.find<TextView>(viewId).text = "$prefix $value"
    }
}