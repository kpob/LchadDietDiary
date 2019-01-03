package pl.kpob.dietdiary.sharedcode.repository

import pl.kpob.dietdiary.sharedcode.model.*

interface Specification<T> {

    fun getCollection(db: Database<T>): List<T>
    fun getItem(db: Database<T>): T? = null
}

class MealsByIdsSpecification(private val ids: Array<String>): Specification<MealDTO> {

    override fun getCollection(db: Database<MealDTO>): List<MealDTO> {
        return db.query(InStrings(MealContract.ID, ids))
    }

}

class MealByIdSpecification(private val id: String) : Specification<MealDTO> {

    override fun getCollection(db: Database<MealDTO>): List<MealDTO> {
        return db.query(EqualsString(MealContract.ID, id))
    }

    override fun getItem(db: Database<MealDTO>): MealDTO? {
        return db.querySingle(EqualsString(MealContract.ID, id))
    }
}

class AllMealsSortedSpecification : Specification<MealDTO> {

    override fun getCollection(db: Database<MealDTO>): List<MealDTO> {
        return db.query(Limit(300), Sorted(Descending(MealContract.TIME)))
    }
}

class MealsWithIngredientSpecification(private val ingredientId: String) : Specification<MealDTO> {

    override fun getCollection(db: Database<MealDTO>): List<MealDTO> {
        return db.query(ContainsString("${MealContract.INGREDIENTS}.${MealIngredientContract.INGREDIENT_ID}", ingredientId))
    }
}


class AllIngredientsSpecification : Specification<IngredientDTO> {

    override fun getCollection(db: Database<IngredientDTO>): List<IngredientDTO> {
        return db.query()
    }
}


class IngredientByIdSpecification(val id: String) : Specification<IngredientDTO> {

    override fun getCollection(db: Database<IngredientDTO>): List<IngredientDTO> {
        return db.query(EqualsString(MealIngredientContract.INGREDIENT_ID, id))
    }

}

class IngredientsByMealTypeSpecification(val type: MealType) : Specification<IngredientDTO> {

    override fun getCollection(db: Database<IngredientDTO>): List<IngredientDTO> {
        val categories = type.filters.map { it.value }.toTypedArray()
        return db.query(
                InInts(IngredientContract.CATEGORY, categories),
                Sorted(Descending(IngredientContract.USE_COUNT)),
                Sorted(Ascending(IngredientContract.NAME))
        )
    }
}


class IngredientsByIdsSpecification(private val ids: Array<String>) : Specification<IngredientDTO> {

    override fun getCollection(db: Database<IngredientDTO>): List<IngredientDTO> {
        return db.query(InStrings(IngredientContract.ID, ids))
    }

}

class AllTagsSpecification(private val ids: Array<String>): Specification<TagDTO> {

    override fun getCollection(db: Database<TagDTO>): List<TagDTO> {
        return db.query()
    }

}

class TemplatesByMealType(private val type: MealType): Specification<MealTemplateDTO> {

    override fun getCollection(db: Database<MealTemplateDTO>): List<MealTemplateDTO> {
        return db.query(EqualsString(MealTemplateContract.TYPE, type.string))
    }
}

class MealsInRangeSpecification(private val start: Long, private val end: Long) : Specification<MealDTO> {

    override fun getCollection(db: Database<MealDTO>): List<MealDTO> {
        return db.query(BetweenLongs(MealContract.TIME, start, end))
    }

}