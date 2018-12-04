package pl.kpob.dietdiary.worker

import android.app.IntentService
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import pl.kpob.dietdiary.App
import pl.kpob.dietdiary.db.IngredientDTO
import pl.kpob.dietdiary.events.IngredientsUpdatedEvent
import pl.kpob.dietdiary.events.MealsUpdatedEvent
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebase.FirebaseValueEventListener
import pl.kpob.dietdiary.firebase.valueEventListener
import pl.kpob.dietdiary.firebaseDb
import pl.kpob.dietdiary.ingredientsRef
import pl.kpob.dietdiary.repo.IngredientRepository
import pl.kpob.dietdiary.repo.IngredientsByIdsSpecification
import pl.kpob.dietdiary.repo.RealmAddTransaction
import pl.kpob.dietdiary.repo.RealmRemoveTransaction
import pl.kpob.dietdiary.usingRealm

class RefreshIngredientsDataService: IntentService("RefreshIngredientsDataService"), AnkoLogger {

    private var job: Job? = null

    private val ingredientsListener = valueEventListener {
        dataChanged { handleData(it) }
    }

    override fun onHandleIntent(intent: Intent?) {
        App.ingredientsSyncing = true
        firebaseDb.ingredientsRef.addValueEventListener(ingredientsListener)
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    private fun FirebaseValueEventListener.handleData(data: DataSnapshot?) {
        job = CoroutineScope(Dispatchers.Default).launch {
            val ingredients = data?.children?.map { it.getValue(FbIngredient::class.java)!! } ?: return@launch

            val ingredientsToDelete = ingredients.filter { it.deleted }
            val ingredientsToSave = ingredients.subtract(ingredientsToDelete).map { it.toRealm() }
            val idsToDelete = ingredientsToDelete.map { it.id }.toTypedArray()

            usingRealm {
                it.executeTransaction {
                    action(it, ingredientsToSave, idsToDelete)
                    firebaseDb.ingredientsRef.removeEventListener(this@handleData)
                    App.ingredientsSyncing = false
                    EventBus.getDefault().post(IngredientsUpdatedEvent)
                }
            }

        }
    }

    private fun action(realm: Realm, ingredientsToSave: List<IngredientDTO>, idsToDelete: Array<String>) {
        val repository = IngredientRepository()
        repository.insert(ingredientsToSave, RealmAddTransaction(realm))
        val spec = IngredientsByIdsSpecification(realm, idsToDelete).also {
            info { it.collection.size }
            it.collection.forEach { info { "${it.id} ${it.name} ${it.calories}" } }
        }
        repository.delete(spec, RealmRemoveTransaction())
    }
}