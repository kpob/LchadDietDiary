package pl.kpob.dietdiary.views

import android.graphics.Color
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import android.view.Menu

/**
 * Created by kpob on 21.10.2017.
 */
interface ToolbarManager {

    val toolbar: Toolbar
    val toolbarMenu: Menu get() = toolbar.menu

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