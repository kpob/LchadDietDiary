package pl.kpob.dietdiary.utils.data

import pl.kpob.dietdiary.db.RealmIngredient
import pl.kpob.dietdiary.repo.RealmChainTransaction
import pl.kpob.dietdiary.repo.RealmRemoveTransaction
import pl.kpob.dietdiary.sharedcode.model.FbIngredient
import pl.kpob.dietdiary.sharedcode.model.Ingredient
import pl.kpob.dietdiary.sharedcode.model.IngredientDTO
import pl.kpob.dietdiary.sharedcode.repository.*
import pl.kpob.dietdiary.sharedcode.utils.IngredientHandler

object AndroidIngredientHandler: IngredientHandler {
    
    override fun save(repo: Repository<IngredientDTO, Ingredient>, ingredientDTO: FbIngredient) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(repo: Repository<IngredientDTO, Ingredient>, ingredient: Ingredient) {
        val spec = IngredientByIdSpecification(ingredient.id)
        val toRemove: RealmIngredient? = repo.data(spec).firstOrNull() as RealmIngredient?
        val t = RealmChainTransaction.of(SingleData(RealmRemoveTransaction(), toRemove))
        repo.executeChainTransaction(t as ChainTransaction<IngredientDTO>)
    }
}