package pl.kpob.dietdiary.worker

import android.app.IntentService
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.joda.time.DateTime
import pl.kpob.dietdiary.*
import pl.kpob.dietdiary.events.MealsUpdatedEvent
import pl.kpob.dietdiary.firebase.FbMeal
import pl.kpob.dietdiary.firebase.FirebaseValueEventListener
import pl.kpob.dietdiary.firebase.valueEventListener
import pl.kpob.dietdiary.repo.MealRepository
import pl.kpob.dietdiary.repo.MealsByIdsSpecification
import pl.kpob.dietdiary.repo.RealmAddTransaction
import pl.kpob.dietdiary.repo.RealmRemoveTransaction

class RefreshMealsDataService: IntentService("RefreshMealsDataService"), AnkoLogger {

    companion object {
        private const val ONE_DAY = 1000 * 60 * 60 *24
    }

    private var job: Job? = null

    private val mealsListener = valueEventListener {
        dataChanged {
            handleData(it)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        App.mealsSyncing = true
        info { "AppPrefs.mealsLastUpdate ${AppPrefs.mealsLastUpdate}" }
        firebaseDb.mealsRef
                .orderByChild("time")
                .apply { if(AppPrefs.mealsLastUpdate != 0L) limitToLast(200) }
                .addValueEventListener(mealsListener)
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    private fun FirebaseValueEventListener.handleData(it: DataSnapshot?) {
        job = CoroutineScope(Dispatchers.Default).launch {

            val meals = it?.children?.map { it.getValue(FbMeal::class.java)!! }?.sortedBy { it.time } ?: return@launch
            val mealsToDelete = meals.filter { it.deleted }
            val mealsToSave = meals.subtract(mealsToDelete).map { it.toRealm() }
            val idsToDelete = mealsToDelete.map { it.id }.toTypedArray()

            debugInfo(meals)

            usingRealm {
                it.executeTransaction {
                    val repository = MealRepository()
                    repository.insert(mealsToSave, RealmAddTransaction(it))
                    if (idsToDelete.isNotEmpty()) {
                        repository.delete(MealsByIdsSpecification(it, idsToDelete), RealmRemoveTransaction())
                    }
                    firebaseDb.mealsRef.removeEventListener(this@handleData)
                    AppPrefs.mealsLastUpdate = meals.last().time
                    App.mealsSyncing = false
                    EventBus.getDefault().post(MealsUpdatedEvent)
                }
            }
        }
    }

    private fun FirebaseValueEventListener.debugInfo(meals: List<FbMeal>) {
        meals.let {
            info { "total #${it.size}" }
            with(it.first()) {
                info { "FIRST" }
                info { "DATE: ${DateTime(time).let { "${it.year}-${it.monthOfYear}-${it.dayOfMonth}" }}" }
                info { "$name $kcal kcal" }
            }
            with(it.last()) {
                info { "LAST" }
                info { "DATE: ${DateTime(time).let { "${it.year}-${it.monthOfYear}-${it.dayOfMonth}" }}" }
                info { "$name $kcal kcal" }
            }
        }
    }

}