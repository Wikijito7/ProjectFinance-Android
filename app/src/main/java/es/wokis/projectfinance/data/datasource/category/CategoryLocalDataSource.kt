package es.wokis.projectfinance.data.datasource.category

import es.wokis.projectfinance.data.bo.category.CategoryBO
import kotlinx.coroutines.flow.Flow

interface CategoryLocalDataSource {
    suspend fun getCategories(): Flow<List<CategoryBO>>
    suspend fun addCategory(category: CategoryBO): Long
    suspend fun updateCategory(category: CategoryBO): Int
    suspend fun removeCategory(categoryId: Long): Int
    suspend fun getCategoryById(categoryId: Long): CategoryBO?
    suspend fun addCategories(categories: List<CategoryBO>)
}