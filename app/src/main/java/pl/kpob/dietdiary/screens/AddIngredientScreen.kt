package pl.kpob.dietdiary.screens

import android.content.Context
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startService
import pl.kpob.dietdiary.ScopedScreen
import pl.kpob.dietdiary.db.IngredientCategory
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.nextId
import pl.kpob.dietdiary.repo.IngredientRepository
import pl.kpob.dietdiary.repo.RealmAddTransaction
import pl.kpob.dietdiary.usingRealm
import pl.kpob.dietdiary.views.AddIngredientView
import pl.kpob.dietdiary.worker.RefreshMealsDataService

/**
 * Created by kpob on 22.10.2017.
 */
class AddIngredientScreen(private val ingredient: FbIngredient? = null) : ScopedScreen<AddIngredientView>(), AnkoLogger {

    private val fbSaver by lazy { FirebaseSaver() }
    private val repo by lazy { IngredientRepository() }

    override fun createView(context: Context?) = AddIngredientView(context!!).also {
        it.toolbarTitle = "Dodaj składnik"
        it.enableHomeAsUp { navigator.goBack() }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)
        launch {
            if (ingredient == null) return@launch
            val category = IngredientCategory.stringValues().indexOfFirst { it == IngredientCategory.fromInt(ingredient.category).label  }
            withContext(uiContext) { view?.preFill(ingredient, category) }
        }
    }

    fun onSaveClick(name: String, kcal: Float, lct: Float, mct: Float, carbohydrates: Float, protein: Float, roughage: Float, salt: Float, type: IngredientCategory) {
        launch {
            val update = ingredient != null
            val newIngredient = if(update) {
                FbIngredient(ingredient!!.id, name, mct, lct, carbohydrates, protein, salt, roughage, kcal, type.value, ingredient.useCount, false)
            } else {
                FbIngredient(nextId(), name, mct, lct, carbohydrates, protein, salt, roughage, kcal, type.value, 0,  false)
            }
            fbSaver.saveIngredient(newIngredient, update)
            usingRealm {
                repo.insert(newIngredient.toRealm(), RealmAddTransaction(it))
            }
            activity.startService<RefreshMealsDataService>()
            withContext(uiContext) {
                navigator.goBack()
            }
        }




    }
}