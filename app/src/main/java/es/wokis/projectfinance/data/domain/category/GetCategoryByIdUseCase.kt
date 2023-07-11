package es.wokis.projectfinance.data.domain.category

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.repository.category.CategoryRepository

interface GetCategoryByIdUseCase {
    suspend operator fun invoke(id: Long): CategoryBO?
}

class GetCategoryByIdUseCaseImpl(private val repository: CategoryRepository) : GetCategoryByIdUseCase {

    override suspend fun invoke(id: Long): CategoryBO? = repository.getCategoryById(id)

}