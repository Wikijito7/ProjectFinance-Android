package es.wokis.projectfinance.data.repository.category

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.datasource.category.CategoryLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface CategoryRepository {
    suspend fun getCategories(): Flow<List<CategoryBO>>
    suspend fun getCategoryById(categoryId: Long): CategoryBO?
    suspend fun addCategory(category: CategoryBO): Long
    suspend fun updateCategory(category: CategoryBO): Int
    suspend fun removeCategory(category: CategoryBO): Int
    suspend fun checkCategories()
    suspend fun insertRemovedCategory()
}

class CategoryRepositoryImpl(private val localDataSource: CategoryLocalDataSource) :
    CategoryRepository {

    private var removedCategory: CategoryBO? = null

    override suspend fun getCategories(): Flow<List<CategoryBO>> = localDataSource.getCategories()

    override suspend fun getCategoryById(categoryId: Long): CategoryBO? =
        localDataSource.getCategoryById(categoryId)

    override suspend fun addCategory(category: CategoryBO): Long =
        localDataSource.addCategory(category)

    override suspend fun updateCategory(category: CategoryBO): Int =
        localDataSource.updateCategory(category)

    override suspend fun removeCategory(category: CategoryBO): Int {
        removedCategory = category
        return localDataSource.removeCategory(category.id)
    }

    override suspend fun insertRemovedCategory()  {
        removedCategory?.let {
            addCategory(it)
        }
    }

    override suspend fun checkCategories() {
        val categories = getCategories()
        if (categories.first().isEmpty()) {
            localDataSource.addCategories(getDefaultCategories())
        }
    }

    private fun getDefaultCategories() = listOf(
        CategoryBO(NONE_CATEGORY_ID, NONE_CATEGORY_TITLE, NONE_CATEGORY_COLOR)
    )

    companion object {
        const val NONE_CATEGORY_ID = 1L
        const val NONE_CATEGORY_TITLE = "NONE"
        const val NONE_CATEGORY_COLOR = "#FFAABB"
    }
}