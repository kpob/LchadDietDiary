package pl.kpob.dietdiary.repo

import pl.kpob.dietdiary.App
import pl.kpob.dietdiary.db.*
import pl.kpob.dietdiary.domain.*
import pl.kpob.dietdiary.mapper.*

/**
 * Created by kpob on 22.10.2017.
 */
class IngredientRepository(private val dao: IngredientDao = App.db.ingredientDao()) {
    private val mapper = IngredientMapper()

    fun getAll(): List<Ingredient> = mapper.map(dao.getAll())
    fun getById(id: String): Ingredient? = mapper.map(dao.getById(id))
    fun getByCategories(categories: IntArray): List<Ingredient> =
            mapper.map(dao.getByCategories(categories))
    fun getByIds(ids: Array<String>): List<Ingredient> = mapper.map(dao.getByIds(ids))
    fun insertOrUpdate(dto: IngredientDTO) = dao.insertOrUpdate(dto)
    fun insertOrUpdateAll(dtos: List<IngredientDTO>) = dao.insertOrUpdateAll(dtos)
    fun deleteById(id: String) = dao.deleteById(id)
    fun deleteByIds(ids: Array<String>) = dao.deleteByIds(ids)
}

class MealRepository(private val dao: MealDao = App.db.mealDao()) {
    fun getAll(): List<Meal> = MealMapper.map(dao.getAllWithIngredients())
    fun getByIds(ids: Array<String>): List<Meal> = MealMapper.map(dao.getByIdsWithIngredients(ids))
    fun getMealsContainingIngredient(ingredientId: String): List<Meal> =
            MealMapper.map(dao.getMealsContainingIngredient(ingredientId))
    fun insertMealWithIngredients(meal: MealDTO, ingredients: List<MealIngredientDTO>) {
        dao.insertMeal(meal)
        dao.insertIngredients(ingredients)
    }
    fun insertAllMealsWithIngredients(meals: List<Pair<MealDTO, List<MealIngredientDTO>>>) {
        dao.insertMeals(meals.map { it.first })
        dao.insertIngredients(meals.flatMap { it.second })
    }
    fun deleteByIds(ids: Array<String>) = dao.deleteByIds(ids)
}

class MealDetailsRepository(private val dao: MealDao = App.db.mealDao()) {
    private val mapper = MealDetailsMapper()

    fun getAll(): List<MealDetails> = mapper.map(dao.getAllWithIngredients())
    fun getById(id: String): MealDetails? = mapper.map(dao.getByIdWithIngredients(id))
    fun getByIds(ids: Array<String>): List<MealDetails> =
            mapper.map(dao.getByIdsWithIngredients(ids))
}

class TagRepository(private val dao: TagDao = App.db.tagDao()) {
    private val mapper = TagMapper()

    fun getAll(): List<Tag> = mapper.map(dao.getAll())
    fun insertOrUpdate(dto: TagDTO) = dao.insertOrUpdate(dto)
    fun delete(dto: TagDTO) = dao.delete(dto)
}
