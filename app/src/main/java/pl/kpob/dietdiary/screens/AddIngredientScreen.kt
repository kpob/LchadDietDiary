package pl.kpob.dietdiary.screens

import android.content.Context
import com.wealthfront.magellan.rx.RxScreen
import pl.kpob.dietdiary.AnkoLogger
import pl.kpob.dietdiary.db.IngredientCategory
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.nextId
import pl.kpob.dietdiary.realmAsyncTransaction
import pl.kpob.dietdiary.repo.IngredientRepository
import pl.kpob.dietdiary.repo.RealmAddTransaction
import pl.kpob.dietdiary.screens.utils.IngredientDataInteractor
import pl.kpob.dietdiary.screens.utils.Traits
import pl.kpob.dietdiary.views.AddIngredientView

/**
 * Created by kpob on 22.10.2017.
 */
class AddIngredientScreen(private val ingredient: FbIngredient? = null)
    : RxScreen<AddIngredientView>(),
        IngredientDataInteractor by Traits.ingredientInteractor(ingredient),
        AnkoLogger {
    
    override fun createView(context: Context?) = AddIngredientView(context!!)

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        view?.let {
            it.toolbarTitle = "Dodaj składnik"
            it.enableHomeAsUp { navigator.goBack() }

            if(ingredient != null) {
                it.preFill(ingredient)
            }
        }
    }

    fun onSaveClick(name: String, kcal: Float, lct: Float, mct: Float, carbohydrates: Float, protein: Float, roughage: Float, salt: Float, type: IngredientCategory) {
        val ingredient = if(ingredient != null) {
            FbIngredient(ingredient.id, name, mct, lct, carbohydrates, protein, salt, roughage, kcal, type.value, ingredient.useCount, false)
        } else {
            FbIngredient(nextId(), name, mct, lct, carbohydrates, protein, salt, roughage, kcal, type.value, 0,  false)
        }

        saveIngredient(ingredient) {
            navigator.goBack()
        }
    }
}