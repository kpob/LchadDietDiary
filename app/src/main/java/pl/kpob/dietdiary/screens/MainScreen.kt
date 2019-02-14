package pl.kpob.dietdiary.screens

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.v7.app.AlertDialog
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.di.components.DaggerMainComponent
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.MainModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.di.modules.common.PopupModule
import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.presenter.MainPresenter
import pl.kpob.dietdiary.sharedcode.view.popup.*
import pl.kpob.dietdiary.views.MainView
import pl.kpob.dietdiary.views.utils.TimePicker
import javax.inject.Inject

/**
 * Created by kpob on 20.10.2017.
 */
class MainScreen : ScopedScreen<MainView>(), PopupDisplayer, AnkoLogger {

    @Inject lateinit var presenter: MainPresenter

    override fun createView(context: Context?): MainView {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .navigatorModule(NavigatorModule(navigator))
                .dataModule(DataModule(realm))
                .popupModule(PopupModule(this))
                .mainModule(MainModule())
                .build().inject(this)

        return MainView(context!!).also {
            it.toolbarTitle = context.getString(R.string.app_name) ?: ""
            it.navigationView.setNavigationItemSelectedListener { menuItem ->
                launch(uiContext) {
                    view?.closeDrawers()
                    delay(250)
                    when (menuItem.itemId) {
                        R.id.nav_ingredients -> presenter.onIngredientListClick()
                        R.id.nav_stats -> navigator.goTo(CalendarScreen())
                    }
                }
                false
            }
            presenter.onShow(it)
        }
    }

    override fun onHide(context: Context?) {
        presenter.onHide()
        super.onHide(context)
    }

    override fun onResume(context: Context?) {
        super.onResume(context)
        presenter.onResume()
    }

    fun onDessertClick() = presenter.onDessertClick()

    fun onDinnerClick() = presenter.onDinnerClick()

    fun onMilkClick() = presenter.onMilkClick()

    fun onItemClick(item: Meal) = presenter.onItemClick(item)

    fun onTimeClick(item: Meal) = presenter.onTimeClick(item)

    fun onDeleteClick(item: Meal) = presenter.onDeleteClick(item)

    fun onEditClick(item: Meal) = navigator.goTo(AddMealScreen(item.type, item))

    fun onAddIngredientClick() = navigator.goTo(AddIngredientScreen())

    fun onLabelClick(meals: List<Meal>) = navigator.goTo(PieChartScreen(meals.map { it.id }))

    fun updateData() = presenter.updateData()

    override fun display(viewModel: PopupViewModel) {
        showDialog {
            when(viewModel) {
                is EditTimePopup -> createEditTimePopup(it, viewModel)
                is DeleteMealPopup -> createDeleteMealPopup(viewModel)
                else -> throw RuntimeException("Try to display unknown dialog $viewModel")
            }
        }
    }

    private fun createEditTimePopup(activity: Activity, viewModel: EditTimePopup): Dialog {
        return TimePicker().dialog(activity, viewModel)
    }

    private fun createDeleteMealPopup(viewModel: DeleteMealPopup): Dialog {
        val data = viewModel.data
        val callbacks = viewModel.callbacks
        return AlertDialog.Builder(activity)
                .setMessage(data.messages)
                .setNegativeButton(callbacks.cancel?.title) { dialog, _ -> callbacks.cancel?.invoke() }
                .setPositiveButton(callbacks.ok?.title) { _, _ -> callbacks.ok?.invoke() }
                .create()
    }

}