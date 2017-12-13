package pl.kpob.dietdiary.repo

import io.realm.Realm
import io.realm.Sort
import pl.kpob.dietdiary.MealType
import pl.kpob.dietdiary.db.*
import pl.kpob.dietdiary.usingRealm

/**
 * Created by kpob on 11.12.2017.
 */
interface Specification<out T> {

    val collection: List<T>
    val single: T? get() = null
}

class MealsByIdsSpecification(private val realm: Realm, private val ids: Array<String>) : Specification<MealDTO> {

    override val collection: List<MealDTO>
        get() = realm.where(MealDTO::class.java).`in`(MealContract.ID, ids).findAll()

}

class MealByIdSpecification(private val realm: Realm, private val id: String) : Specification<MealDTO> {

    override val collection: List<MealDTO>
        get() = realm.where(MealDTO::class.java).equalTo(MealContract.ID, id).findAll()

    override val single: MealDTO?
        get() = realm.where(MealDTO::class.java).equalTo(MealContract.ID, id).findFirst()
}

class AllMealsSortedSpecification(private val realm: Realm): Specification<MealDTO> {

    override val collection: List<MealDTO>
        get() = realm.where(MealDTO::class.java).findAllSorted(MealContract.TIME, Sort.DESCENDING)
}

class MealsWithIngredientSpecification(private val realm: Realm, private val ingredientId: String) : Specification<MealDTO> {

    override val collection: List<MealDTO>
        get() = realm.where(MealDTO::class.java)
                    .contains("${MealContract.INGREDIENTS}.${MealIngredientContract.INGREDIENT_ID}", ingredientId)
                    .findAll()
}


class AllIngredientsSpecification(private val realm: Realm) : Specification<IngredientDTO> {

    override val collection: List<IngredientDTO>
        get() = realm.where(IngredientDTO::class.java).findAll()
}


class IngredientByIdSpecification(private val realm: Realm, val id: String) : Specification<IngredientDTO> {

    override val collection: List<IngredientDTO>
        get() = realm.where(IngredientDTO::class.java).findAll()

}

class IngredientsByMealTypeSpecification(private val realm: Realm, val type: MealType) : Specification<IngredientDTO> {

    override val collection: List<IngredientDTO>
        get() {
            val categories = type.filters.map { it.value }.toTypedArray()
            return realm.where(IngredientDTO::class.java)
                    .`in`(IngredientContract.CATEGORY, categories)
                    .findAllSorted(arrayOf(IngredientContract.USE_COUNT, IngredientContract.NAME), arrayOf(Sort.DESCENDING, Sort.ASCENDING))
        }
}


class IngredientsByIdsSpecification(private val realm: Realm, private val ids: Array<String>) : Specification<IngredientDTO> {

    override val collection: List<IngredientDTO>
        get() = realm.where(IngredientDTO::class.java).`in`(IngredientContract.ID, ids).findAll()

}