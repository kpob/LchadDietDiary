package pl.kpob.dietdiary.screens

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.widget.EditText
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import pl.kpob.dietdiary.R
import pl.kpob.dietdiary.di.components.DaggerAddComponent
import pl.kpob.dietdiary.di.modules.AddMealModule
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.di.modules.common.PopupModule
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.model.MealType
import pl.kpob.dietdiary.sharedcode.presenter.AddMealPresenter
import pl.kpob.dietdiary.sharedcode.view.popup.*
import pl.kpob.dietdiary.views.AddMealView
import pl.kpob.dietdiary.views.utils.TimePicker
import javax.inject.Inject

/**
 * Created by kpob on 20.10.2017.
 */
class AddMealScreen(
        private val type: MealType,
        private val meal: Meal? = null) : ScopedScreen<AddMealView>(), AnkoLogger, PopupDisplayer {

    @Inject lateinit var presenter: AddMealPresenter

    override fun createView(context: Context?) = AddMealView(context!!).also {
        it.enableHomeAsUp { navigator.handleBack() }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)
        DaggerAddComponent.builder()
                .popupModule(PopupModule(this))
                .addMealModule(AddMealModule(type, meal))
                .navigatorModule(NavigatorModule(navigator))
                .dataModule(DataModule(realm))
                .appComponent(appComponent)
                .build().inject(this)
        presenter.onShow(view)
    }

    fun onAddClick(data: List<Pair<Ingredient, Float>>, left: Float) = presenter.onAddClick(data, left)

    fun onTimeEditClick() = presenter.onTimeEditClick()

    fun addTemplate(ingredients: List<Ingredient>) = presenter.addTemplate(ingredients)

    fun loadTemplate() = presenter.loadTemplate()

    fun updateTotalWeight(value: Float) = presenter.updateTotalWeight(value)

    fun onAddRowClick() = presenter.onAddRowClick()

    override fun display(viewModel: PopupViewModel) {
        showDialog {
            when(viewModel) {
                is EditTimePopup -> createEditTimePopup(it, viewModel)
                is AddTemplatePopup -> createAddTemplatePopup(viewModel)
                is LoadTemplatePopup -> createLoadTemplatePopup(viewModel)
                else -> throw RuntimeException("Try to display unknown dialog $viewModel")
            }
        }
    }

    private fun createEditTimePopup(activity: Activity, viewModel: EditTimePopup): Dialog {
        return TimePicker().dialog(activity, viewModel)
    }

    private fun createAddTemplatePopup(viewModel: AddTemplatePopup): Dialog {
        val data = viewModel.data
        val callbacks = viewModel.callbacks
        return AlertDialog.Builder(activity).apply {
            setTitle(data.title)

            var nameField: EditText? = null
            setView(activity.layoutInflater.inflate(R.layout.view_save_template, null).apply {
                nameField = find(R.id.template_name)
            })

            setNegativeButton(callbacks.cancel?.title) { dialog, _ -> callbacks.cancel?.invoke() }
            setPositiveButton(callbacks.ok?.title) { _, _ ->
                val name = nameField?.text?.toString() ?: ""
                callbacks.ok?.invoke(AddTemplatePayload(name))
            }
        }.create()

    }


    private fun createLoadTemplatePopup(viewModel: LoadTemplatePopup): Dialog {
        val data = viewModel.data
        val callbacks = viewModel.callbacks
        return AlertDialog.Builder(activity).apply {
            setTitle(data.title)
            setSingleChoiceItems(viewModel.names, 0) { dialog, which ->
                callbacks.ok?.invoke(LoadTemplatePayload(which))
                dialog.dismiss()
            }
        }.create()
    }

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    companion object {
        fun Ingredient.toString(): String = name
    }

}