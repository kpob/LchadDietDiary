package pl.kpob.dietdiary.db

import androidx.room.*

@Dao
interface IngredientDao {

    @Query("SELECT * FROM IngredientDTO")
    fun getAll(): List<IngredientDTO>

    @Query("SELECT * FROM IngredientDTO WHERE id = :id")
    fun getById(id: String): IngredientDTO?

    @Query("SELECT * FROM IngredientDTO WHERE category IN (:categories) ORDER BY useCount DESC, name ASC")
    fun getByCategories(categories: IntArray): List<IngredientDTO>

    @Query("SELECT * FROM IngredientDTO WHERE id IN (:ids)")
    fun getByIds(ids: Array<String>): List<IngredientDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(ingredient: IngredientDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateAll(ingredients: List<IngredientDTO>)

    @Query("DELETE FROM IngredientDTO WHERE id = :id")
    fun deleteById(id: String)

    @Query("DELETE FROM IngredientDTO WHERE id IN (:ids)")
    fun deleteByIds(ids: Array<String>)
}
