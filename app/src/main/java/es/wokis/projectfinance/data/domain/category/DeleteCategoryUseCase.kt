package es.wokis.projectfinance.data.domain.category

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.repository.category.CategoryRepository

interface DeleteCategoryUseCase {
    suspend operator fun invoke(category: CategoryBO): Int
}

class DeleteCategoryUseCaseImpl(private val repository: CategoryRepository) : DeleteCategoryUseCase {

    override suspend fun invoke(category: CategoryBO): Int = repository.removeCategory(category)

}