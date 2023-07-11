package es.wokis.projectfinance.data.domain.category

import es.wokis.projectfinance.data.repository.category.CategoryRepository

interface CheckCategoriesUseCase {
    suspend operator fun invoke()
}

class CheckCategoriesUseCaseImpl(private val repository: CategoryRepository) : CheckCategoriesUseCase {

    override suspend fun invoke() = repository.checkCategories()

}