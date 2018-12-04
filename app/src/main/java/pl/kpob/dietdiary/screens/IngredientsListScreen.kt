package pl.kpob.dietdiary.screens

import android.content.Context
import io.realm.Realm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startService
import org.jetbrains.anko.toast
import pl.kpob.dietdiary.ScopedScreen
import pl.kpob.dietdiary.db.IngredientCategory
import pl.kpob.dietdiary.domain.Ingredient
import pl.kpob.dietdiary.domain.IngredientsGroups
import pl.kpob.dietdiary.domain.IngredientsViewModel
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebaseDb
import pl.kpob.dietdiary.ingredientsRef
import pl.kpob.dietdiary.repo.*
import pl.kpob.dietdiary.views.IngredientsListView
import pl.kpob.dietdiary.worker.RefreshIngredientsDataService
import pl.kpob.dietdiary.worker.RefreshMealsDataService

/**
 * Created by kpob on 22.10.2017.
 */
class IngredientsListScreen: ScopedScreen<IngredientsListView>(), AnkoLogger {

    private val ingredientRepository by lazy { IngredientRepository() }
    private val mealRepository by lazy { MealRepository() }

    override fun createView(context: Context?) = IngredientsListView(context!!).apply {
        toolbarTitle = "Składniki"
        enableHomeAsUp { navigator.goBack() }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)

        launch {
            val ingredients = queryData()
            withContext(uiContext) {
                view?.initList(ingredients)
            }
        }
    }

    fun onDeleteClick(item: Ingredient) {
        launch {
            val realm = Realm.getDefaultInstance()
            val spec = MealsWithIngredientSpecification(realm, item.id)
            val count = mealRepository.query(spec).size
            when(count) {
                0 -> {
                    deleteIngredient(realm, item)
                    delay(500)
                    val ingredients = queryData()
                    withContext(uiContext) {
                        view?.updateList(ingredients)
                        activity.startService<RefreshIngredientsDataService>()
                    }
                }
                else -> withContext(uiContext) { view.context.toast("Nie można usunąć - występuje w $count posiłkach") }
            }
            realm.close()
        }
    }

    fun onEditClick(item: Ingredient) {
        navigator.goTo(AddIngredientScreen(item.toFirebase()))
    }

    private fun deleteIngredient(realm: Realm, item: Ingredient)  {
        val toUpdate = mapOf<String, Any>(item.id to item.toFirebase(true))
        firebaseDb.ingredientsRef.updateChildren(toUpdate)

        val spec = IngredientByIdSpecification(realm, item.id)
        realm.executeTransaction {
            ingredientRepository.delete(spec, RealmRemoveTransaction())
        }
    }

    private fun queryData(): IngredientsViewModel {
        val ingredients = ingredientRepository.withRealmQuery {  AllIngredientsSpecification(it) }
        val groups = ingredients.groupBy { it.category }.toList()
        val groupsCount = groups.size
        val ranges = (0 until groupsCount).map {
            (1 + it + groups.take(it).map { it.second }.flatten().count())..(groups.take(it + 1).map { it.second }.flatten().count() + it)
        }
        return IngredientsViewModel(
                groups.map { IngredientsGroups(IngredientCategory.fromInt(it.first), it.second) },
                groupsCount,
                ranges
        )
    }

    fun Ingredient.toFirebase(deleted: Boolean = false): FbIngredient = FbIngredient(
        id, name, mtc, lct, carbohydrates, protein, salt, roughage, calories, category, useCount, deleted
    )
}