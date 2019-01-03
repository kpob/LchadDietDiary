package pl.kpob.dietdiary.screens

import android.content.Context
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.di.components.DaggerIngredientsComponent
import pl.kpob.dietdiary.di.modules.IngredientsModule
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.presenter.IngredientListPresenter
import pl.kpob.dietdiary.views.IngredientsListView
import javax.inject.Inject

/**
 * Created by kpob on 22.10.2017.
 */
class IngredientsListScreen: ScopedScreen<IngredientsListView>(), AnkoLogger {

    @Inject lateinit var presenter: IngredientListPresenter

    override fun createView(context: Context?) = IngredientsListView(context!!).apply {
        enableHomeAsUp { navigator.goBack() }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)
        DaggerIngredientsComponent.builder()
                .appComponent(appComponent)
                .dataModule(DataModule(realm))
                .navigatorModule(NavigatorModule(navigator))
                .ingredientsModule(IngredientsModule())
                .build().inject(this)

        presenter.onShow(view)
    }

    fun onDeleteClick(item: Ingredient) = presenter.onDeleteClick(item)

    fun onEditClick(item: Ingredient) = presenter.onEditClick(item)

}