package pl.kpob.dietdiary.utils.data

import pl.kpob.dietdiary.db.RealmIngredient
import pl.kpob.dietdiary.repo.RealmAddTransaction
import pl.kpob.dietdiary.repo.RealmChainTransaction
import pl.kpob.dietdiary.repo.RealmRemoveTransaction
import pl.kpob.dietdiary.sharedcode.model.*
import pl.kpob.dietdiary.sharedcode.repository.*
import pl.kpob.dietdiary.sharedcode.utils.IngredientHandler

object AndroidIngredientHandler: IngredientHandler {

    override fun asFbModel(ingredient: Ingredient): FbIngredient {
        return ingredient.toFbModel()
    }

    override fun save(repo: Repository<IngredientDTO, Ingredient>, ingredientDTO: FbIngredient) {
        val t = RealmChainTransaction.of(SingleData(RealmAddTransaction(), ingredientDTO.toRealm()))
        repo.executeChainTransaction(t as ChainTransaction<IngredientDTO>)
    }

    override fun delete(repo: Repository<IngredientDTO, Ingredient>, ingredient: Ingredient) {
        val spec = IngredientByIdSpecification(ingredient.id)
        val toRemove: RealmIngredient? = repo.data(spec).firstOrNull() as RealmIngredient?
        val t = RealmChainTransaction.of(SingleData(RealmRemoveTransaction(), toRemove))
        repo.executeChainTransaction(t as ChainTransaction<IngredientDTO>)
    }

    private fun FbIngredient.toRealm(): RealmIngredient {
        return RealmIngredient(id, name, mtc, lct, carbohydrates, protein, salt, roughage, calories, category, useCount)
    }

    private fun Ingredient.toFbModel(): FbIngredient {
        return FbIngredient(id, name, mtc, lct, carbohydrates, protein, salt, roughage, calories, category, useCount)
    }
}