package es.wokis.projectfinance.data.local.category

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.dao.category.CategoryDAO
import es.wokis.projectfinance.data.datasource.category.CategoryLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryLocalDataSourceImpl @Inject constructor(private val dao: CategoryDAO) :
    CategoryLocalDataSource {

    override suspend fun getCategories(): Flow<List<CategoryBO>> = dao.getCategories().map { it.toBO() }

    override suspend fun addCategory(category: CategoryBO): Long = dao.addCategory(category.toDBO())

    override suspend fun updateCategory(category: CategoryBO): Int = dao.updateCategory(category.toDBO())

    override suspend fun removeCategory(categoryId: Long): Int = dao.removeCategory(categoryId)

    override suspend fun getCategoryById(categoryId: Long): CategoryBO? = dao.getCategoryById(categoryId)?.toBO()

    override suspend fun addCategories(categories: List<CategoryBO>) = dao.addCategories(categories.toDBO())

}