package pl.kpob.dietdiary.screens.utils

import pl.kpob.dietdiary.firebase.FbIngredient
import pl.kpob.dietdiary.firebase.FirebaseSaver
import pl.kpob.dietdiary.realmAsyncTransaction
import pl.kpob.dietdiary.repo.IngredientRepository
import pl.kpob.dietdiary.repo.RealmAddTransaction

/**
 * Created by kpob on 16.03.2018.
 */
interface IngredientDataInteractor {

    fun saveIngredient(ingredient: FbIngredient, cb: () -> Unit)
}

internal class IIngredientDataInteractor(private val ingredient: FbIngredient? = null): IngredientDataInteractor {

    private val fbSaver by lazy { FirebaseSaver() }
    private val repo by lazy { IngredientRepository() }


    override fun saveIngredient(ingredient: FbIngredient, cb: () -> Unit) {
        fbSaver.saveIngredient(ingredient, this.ingredient != null)

        realmAsyncTransaction(
                transaction = { repo.insert(ingredient.toRealm(), RealmAddTransaction(it)) },
                callback = cb
        )
    }

}