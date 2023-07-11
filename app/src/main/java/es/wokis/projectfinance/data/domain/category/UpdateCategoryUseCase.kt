package es.wokis.projectfinance.data.domain.category

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.repository.category.CategoryRepository

interface UpdateCategoryUseCase {
    suspend operator fun invoke(category: CategoryBO): Int
}

class UpdateCategoryUseCaseImpl(private val repository: CategoryRepository) : UpdateCategoryUseCase {

    override suspend fun invoke(category: CategoryBO): Int = repository.updateCategory(category)

}