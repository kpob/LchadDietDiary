package pl.kpob.dietdiary.screens

import android.content.Context
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.di.components.DaggerAddIngredientComponent
import pl.kpob.dietdiary.di.modules.AddIngredientModule
import pl.kpob.dietdiary.di.modules.common.DataModule
import pl.kpob.dietdiary.di.modules.common.NavigatorModule
import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.IngredientCategory
import pl.kpob.dietdiary.sharedcode.presenter.AddIngredientPresenter
import pl.kpob.dietdiary.views.AddIngredientView
import javax.inject.Inject

/**
 * Created by kpob on 22.10.2017.
 */
class AddIngredientScreen(private val ingredient: FbIngredient? = null) : ScopedScreen<AddIngredientView>(), AnkoLogger {

    @Inject lateinit var presenter: AddIngredientPresenter

    override fun createView(context: Context?): AddIngredientView {
        DaggerAddIngredientComponent.builder()
                .appComponent(appComponent)
                .dataModule(DataModule(realm))
                .navigatorModule(NavigatorModule(navigator))
                .addIngredientModule(AddIngredientModule(ingredient))
                .build().inject(this)
        return AddIngredientView(context!!).also {
            it.enableHomeAsUp { navigator.goBack() }
            presenter.onShow(it)
        }
    }

    fun onSaveClick(name: String, kcal: Float, lct: Float, mct: Float, carbohydrates: Float, protein: Float, roughage: Float, salt: Float, type: IngredientCategory) {
        presenter.onSaveClick(name, kcal, lct, mct, carbohydrates, protein, roughage, salt, type)
    }

}