package es.wokis.projectfinance.data.domain.category

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.repository.category.CategoryRepository

interface CreateCategoryUseCase {
    suspend operator fun invoke(category: CategoryBO): Long
}

class CreateCategoryUseCaseImpl(private val repository: CategoryRepository) : CreateCategoryUseCase {

    override suspend fun invoke(category: CategoryBO): Long = repository.addCategory(category)

}