package pl.kpob.dietdiary.utils.data

import io.realm.RealmList
import pl.kpob.dietdiary.db.RealmMeal
import pl.kpob.dietdiary.db.RealmMealIngredient
import pl.kpob.dietdiary.repo.RealmAddTransaction
import pl.kpob.dietdiary.repo.RealmChainTransaction
import pl.kpob.dietdiary.sharedcode.model.FbMeal
import pl.kpob.dietdiary.sharedcode.model.Meal
import pl.kpob.dietdiary.sharedcode.model.MealDTO
import pl.kpob.dietdiary.sharedcode.repository.ChainTransaction
import pl.kpob.dietdiary.sharedcode.repository.Repository
import pl.kpob.dietdiary.sharedcode.repository.SingleData
import pl.kpob.dietdiary.sharedcode.utils.MealSaver

object AndroidMealSaver: MealSaver {

    override fun save(repo: Repository<MealDTO, Meal>, meal: FbMeal) {
        val t = RealmChainTransaction.of(SingleData(RealmAddTransaction(), meal.toRealm()))
        repo.executeChainTransaction(t as ChainTransaction<MealDTO>)
    }

    private fun FbMeal.toRealm(): RealmMeal {
        val list = RealmList<RealmMealIngredient>()
        val i = ingredients.map { RealmMealIngredient(it.ingredientId, it.weight) }
        list.addAll(i)
        return RealmMeal(id, time, name, list)
    }
}