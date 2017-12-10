package pl.kpob.dietdiary.screens

import android.content.Context
import android.os.Handler
import com.wealthfront.magellan.rx.RxScreen
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.db.MealDTO
import pl.kpob.dietdiary.views.IngredientsListView

/**
 * Created by kpob on 22.10.2017.
 */
class IngredientsListScreen: RxScreen<IngredientsListView>(), AnkoLogger {

    val data: List<IngredientDTO>
        get() = usingRealm { it.copyFromRealm(it.where(IngredientDTO::class.java).findAll()) }


    private val handler = Handler()

    override fun createView(context: Context?): IngredientsListView {
        return IngredientsListView(context!!)
    }

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)

        view?.let {
            it.toolbarTitle = "Składniki"
            it.enableHomeAsUp { navigator.goBack() }
            it.initList(data)
        }
    }

    fun onDeleteClick(item: IngredientDTO) {

        usingRealm {
            val mealsContainingIngredientCount = it.where(MealDTO::class.java).contains("ingredients.ingredientId", item.id).count()
            if(mealsContainingIngredientCount == 0L) {
                val toUpdate = mapOf<String, Any>(item.id to item.toFirebase(true))
                firebaseDb.ingredientsRef.updateChildren(toUpdate)

                it.executeTransactionAsync(
                        { it.where(IngredientDTO::class.java).equalTo("id", item.id).findAll().deleteAllFromRealm() },
                        { handler.post { view.updateList(data) } },
                        { view.context.toast("Nie da się usunąć składnika") }
                )
            } else {
                view.context.toast("Nie można usunąć - występuje w $mealsContainingIngredientCount posiłkach")
            }

        }
    }

    fun onEditClick(item: IngredientDTO) {
        navigator.goTo(AddIngredientScreen(item.toFirebase()))
    }
}