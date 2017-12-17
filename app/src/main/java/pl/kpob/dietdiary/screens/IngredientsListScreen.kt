package pl.kpob.dietdiary.screens

import android.content.Context
import android.os.Handler
import com.wealthfront.magellan.rx.RxScreen
import io.realm.Realm
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebaseDb
import pl.kpob.dietdiary.ingredientsRef
import pl.kpob.dietdiary.repo.*
import pl.kpob.dietdiary.usingRealm
import pl.kpob.dietdiary.views.IngredientsListView

/**
 * Created by kpob on 22.10.2017.
 */
class IngredientsListScreen: RxScreen<IngredientsListView>(), AnkoLogger {

    private val ingredientRepository by lazy { IngredientRepository() }
    private val mealRepository by lazy { MealRepository() }
    private val data: List<Ingredient> get() = ingredientRepository.withRealmQuery {  AllIngredientsSpecification(it) }

    private val handler = Handler()

    override fun createView(context: Context?) = IngredientsListView(context!!)

    override fun onSubscribe(context: Context?) {
        super.onSubscribe(context)
        view?.let {
            it.toolbarTitle = "Składniki"
            it.enableHomeAsUp { navigator.goBack() }
            it.initList(data)
        }
    }

    fun onDeleteClick(item: Ingredient) = usingRealm {
        val spec = MealsWithIngredientSpecification(it, item.id)
        val count = mealRepository.query(spec).size
        when(count) {
            0 -> deleteIngredient(it, item)
            else -> view.context.toast("Nie można usunąć - występuje w $count posiłkach")
        }
    }

    private fun deleteIngredient(realm: Realm, item: Ingredient) {
        val toUpdate = mapOf<String, Any>(item.id to item.toFirebase(true))
        firebaseDb.ingredientsRef.updateChildren(toUpdate)

        val spec = IngredientByIdSpecification(realm, item.id)
        realm.executeTransaction {
            ingredientRepository.delete(spec, RealmRemoveTransaction())
        }
        handler.post { view.updateList(data) }
    }

    fun onEditClick(item: Ingredient) {
        navigator.goTo(AddIngredientScreen(item.toFirebase()))
    }

    fun Ingredient.toFirebase(deleted: Boolean = false): FbIngredient = FbIngredient(
        id, name, mtc, lct, carbohydrates, protein, salt, roughage, calories, category, useCount, deleted
    )
}