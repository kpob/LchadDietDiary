package pl.kpob.dietdiary.db

import androidx.room.*

@Dao
interface TagDao {

    @Query("SELECT * FROM TagDTO")
    fun getAll(): List<TagDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(tag: TagDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateAll(tags: List<TagDTO>)

    @Delete
    fun delete(tag: TagDTO)
}
