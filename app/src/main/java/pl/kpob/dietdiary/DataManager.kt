package pl.kpob.dietdiary

import com.google.firebase.database.DataSnapshot
import io.realm.Realm
import io.realm.RealmList
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.AnkoLogger
import pl.kpob.dietdiary.db.RealmIngredient
import pl.kpob.dietdiary.db.RealmMeal
import pl.kpob.dietdiary.db.RealmMealIngredient
import pl.kpob.dietdiary.firebase.valueEventListener
import pl.kpob.dietdiary.repo.*
import pl.kpob.dietdiary.sharedcode.eventbus.IngredientsUpdateEvent
import pl.kpob.dietdiary.sharedcode.eventbus.MealsUpdateEvent
import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.repository.*
import kotlin.coroutines.CoroutineContext

object DataManager : CoroutineScope, AnkoLogger {

    private var parentJob: Job? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + parentJob!!


    private val ingredientsListener = valueEventListener { dataChanged { refreshIngredients(it) } }
    private val mealsListener = valueEventListener { dataChanged { refreshMeals(it) } }

    fun start() {
        parentJob = Job()
        App.ingredientsSyncing = true
        App.mealsSyncing = true

        firebaseDb.ingredientsRef.addValueEventListener(ingredientsListener)
        firebaseDb.mealsRef.addValueEventListener(mealsListener)
    }

    fun stop() {
        parentJob?.cancel()
        firebaseDb.ingredientsRef.removeEventListener(ingredientsListener)
        firebaseDb.mealsRef.removeEventListener(mealsListener)
    }

    private fun refreshMeals(snapshot: DataSnapshot?) {
        App.mealsSyncing = true
        EventBus.getDefault().post(MealsUpdateEvent)
        launch {
            val meals = snapshot?.children?.map { it.getValue(FbMeal::class.java)!! }?.sortedBy { it.time }
                    ?: return@launch

            val mealsToDelete = meals.filter { it.deleted }
            val mealsToSave = meals.subtract(mealsToDelete).map { it.toRealm() }
            val idsToDelete = mealsToDelete.map { it.id }.toTypedArray()

            val realm = Realm.getDefaultInstance()
            val repo = RepositoriesFactory.mealsRepository(realm, listOf())

            val dataToDelete: List<RealmMeal> = if (idsToDelete.isNotEmpty()) {
                repo.data(MealsByIdsSpecification(idsToDelete) as Specification<RealmMeal>)
            } else {
                listOf()
            }

            val transaction = RealmChainTransaction.of(
                    MultiData(RealmAddTransaction(), mealsToSave),
                    MultiData(RealmRemoveTransaction(), dataToDelete)
            )
            repo.executeChainTransaction(transaction)

            AppPrefs.mealsLastUpdate = meals.last().time
            App.mealsSyncing = false
            realm.close()
        }
    }

    private fun refreshIngredients(snapshot: DataSnapshot?) {
        App.ingredientsSyncing = true
        EventBus.getDefault().post(IngredientsUpdateEvent)

        launch {
            val ingredients = snapshot?.children?.map { it.getValue(FbIngredient::class.java)!! }
                    ?: return@launch

            val ingredientsToDelete = ingredients.filter { it.deleted }
            val ingredientsToSave = ingredients.subtract(ingredientsToDelete).map { it.toRealm() }
            val idsToDelete = ingredientsToDelete.map { it.id }.toTypedArray()

            val realm = Realm.getDefaultInstance()
            val repo = RepositoriesFactory.ingredientsRepository(realm)

            val transaction = RealmChainTransaction.of(
                    MultiData(RealmAddTransaction(), ingredientsToSave),
                    MultiData(RealmRemoveTransaction(), repo.data(IngredientsByIdsSpecification(idsToDelete) as Specification<RealmIngredient>))
            )
            repo.executeChainTransaction(transaction)

            App.ingredientsSyncing = false
            realm.close()
        }
    }

    private fun FbIngredient.toRealm() = RealmIngredient(id, name, mtc, lct, carbohydrates, protein, salt, roughage, calories, category, useCount)

    private fun FbMeal.toRealm(): RealmMeal {
        val list = RealmList<RealmMealIngredient>()
        val i = ingredients.map { RealmMealIngredient(it.ingredientId, it.weight) }
        list.addAll(i)
        return RealmMeal(id, time, name, list)
    }

}