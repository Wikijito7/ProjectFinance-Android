package es.wokis.projectfinance.data.dao.category

import androidx.room.*
import es.wokis.projectfinance.data.local.category.dbo.CategoryDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAO {
    @Query("SELECT * FROM category")
    fun getCategories(): Flow<List<CategoryDBO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCategory(category: CategoryDBO): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCategories(categories: List<CategoryDBO>)

    @Query("DELETE FROM category where categoryId = :id")
    fun removeCategory(id: Long): Int

    @Update
    fun updateCategory(category: CategoryDBO): Int

    @Query("SELECT * FROM category WHERE categoryId = :categoryId")
    fun getCategoryById(categoryId: Long): CategoryDBO?
}