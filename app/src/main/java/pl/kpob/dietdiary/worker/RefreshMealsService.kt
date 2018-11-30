package pl.kpob.dietdiary.worker

import android.app.IntentService
import android.content.Intent
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import pl.kpob.dietdiary.App
import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebase.valueEventListener
import pl.kpob.dietdiary.firebaseDb
import pl.kpob.dietdiary.ingredientsRef
import pl.kpob.dietdiary.repo.IngredientRepository
import pl.kpob.dietdiary.repo.IngredientsByIdsSpecification
import pl.kpob.dietdiary.repo.RealmAddTransaction
import pl.kpob.dietdiary.repo.RealmRemoveTransaction
import pl.kpob.dietdiary.usingRealm

class RefreshMealsService: IntentService("RefreshMealsService"), AnkoLogger {

    private val ingredientsListener = valueEventListener {

        dataChanged {
            val ingredients = it?.children?.map { it.getValue(FbIngredient::class.java)!! } ?: return@dataChanged
            val ingredientsToDelete = ingredients.filter { it.deleted }
            val ingredientsToSave = ingredients.subtract(ingredientsToDelete).map { it.toRealm() }
            val idsToDelete = ingredientsToDelete.map { it.id }.toTypedArray()

            info { "to del" }
            idsToDelete.forEach { info { "id: $it" } }
            val repository = IngredientRepository()

            usingRealm {
                it.executeTransactionAsync {
                    App.ingredientsSyncing = false
                    repository.insert(ingredientsToSave, RealmAddTransaction(it))
                    val spec = IngredientsByIdsSpecification(it, idsToDelete)
                    info { spec.collection.size }
                    spec.collection.forEach {
                        info { "${it.id} ${it.name} ${it.calories}" }
                    }
                    repository.delete(spec, RealmRemoveTransaction())
                }
            }
            firebaseDb.ingredientsRef.removeEventListener(this)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        App.ingredientsSyncing = true
        firebaseDb.ingredientsRef.addValueEventListener(ingredientsListener)
    }
}