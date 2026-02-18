package pl.kpob.dietdiary.db

import androidx.room.*

@Dao
interface MealDao {

    @Transaction
    @Query("SELECT * FROM MealDTO ORDER BY time DESC")
    fun getAllWithIngredients(): List<MealWithIngredients>

    @Transaction
    @Query("SELECT * FROM MealDTO WHERE id = :id")
    fun getByIdWithIngredients(id: String): MealWithIngredients?

    @Transaction
    @Query("SELECT * FROM MealDTO WHERE id IN (:ids)")
    fun getByIdsWithIngredients(ids: Array<String>): List<MealWithIngredients>

    @Transaction
    @Query("""
        SELECT DISTINCT m.* FROM MealDTO m
        INNER JOIN MealIngredientDTO mi ON m.id = mi.mealId
        WHERE mi.ingredientId = :ingredientId
    """)
    fun getMealsContainingIngredient(ingredientId: String): List<MealWithIngredients>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeal(meal: MealDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeals(meals: List<MealDTO>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIngredients(ingredients: List<MealIngredientDTO>)

    @Query("DELETE FROM MealDTO WHERE id IN (:ids)")
    fun deleteByIds(ids: Array<String>)
}
