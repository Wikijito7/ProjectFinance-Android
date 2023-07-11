package es.wokis.projectfinance.data.domain.category

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.repository.category.CategoryRepository
import kotlinx.coroutines.flow.Flow

interface GetCategoriesUseCase {
    suspend operator fun invoke(): Flow<List<CategoryBO>>
}

class GetCategoriesUseCaseImpl(private val repository: CategoryRepository) : GetCategoriesUseCase {

    override suspend fun invoke(): Flow<List<CategoryBO>> = repository.getCategories()

}