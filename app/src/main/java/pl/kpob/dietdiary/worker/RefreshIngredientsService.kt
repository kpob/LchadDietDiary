package pl.kpob.dietdiary.worker

import android.app.IntentService
import android.content.Intent
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import pl.kpob.dietdiary.App
import pl.kpob.dietdiary.firebase.FbMeal
import pl.kpob.dietdiary.firebase.valueEventListener
import pl.kpob.dietdiary.firebaseDb
import pl.kpob.dietdiary.mealsRef
import pl.kpob.dietdiary.repo.MealRepository
import pl.kpob.dietdiary.repo.MealsByIdsSpecification
import pl.kpob.dietdiary.repo.RealmAddTransaction
import pl.kpob.dietdiary.repo.RealmRemoveTransaction
import pl.kpob.dietdiary.usingRealm

class RefreshIngredientsService: IntentService("RefreshIngredientsService"), AnkoLogger {

    private val mealsListener = valueEventListener {

        dataChanged {
            val meals = it?.children?.map { it.getValue(FbMeal::class.java)!! } ?: return@dataChanged
            val mealsToDelete = meals.filter { it.deleted }
            val mealsToSave = meals.subtract(mealsToDelete).map { it.toRealm() }
            val idsToDelete = mealsToDelete.map { it.id }.toTypedArray()

            meals.firstOrNull { it.id.contains("ee4c235f") }?.let {
                info { it }
            }

            usingRealm {
                it.executeTransactionAsync {
                    App.mealsSyncing = false
                    val repository = MealRepository()
                    repository.insert(mealsToSave, RealmAddTransaction(it))
                    if(idsToDelete.isNotEmpty()) {
                        repository.delete(MealsByIdsSpecification(it, idsToDelete), RealmRemoveTransaction())
                    }
                }
            }
            firebaseDb.mealsRef.removeEventListener(this)

        }
    }
    override fun onHandleIntent(intent: Intent?) {
        App.mealsSyncing = true
        firebaseDb.mealsRef.orderByChild("time").limitToLast(200).addValueEventListener(mealsListener)
    }
}