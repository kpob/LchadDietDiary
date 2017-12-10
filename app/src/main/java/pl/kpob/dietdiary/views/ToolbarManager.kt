package pl.kpob.dietdiary.views

import android.graphics.Color
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar

/**
 * Created by kpob on 21.10.2017.
 */
interface ToolbarManager {

    val toolbar: Toolbar

    var toolbarTitle: String
        get() = toolbar.title.toString()
        set(value) {
            toolbar.title = value
        }
    fun enableHomeAsUp(up: () -> Unit) {
        toolbar.navigationIcon = createUpDrawable()
        toolbar.setNavigationOnClickListener { up() }
    }

    fun initMenu(menuId: Int, onIdSelect: (Int) -> Unit) {
        toolbar.inflateMenu(menuId)
        toolbar.setOnMenuItemClickListener {
            onIdSelect(it.itemId)
            true
        }
    }

    private fun createUpDrawable() = DrawerArrowDrawable(toolbar.context).apply {
        progress = 1f
        color = Color.WHITE
    }

}