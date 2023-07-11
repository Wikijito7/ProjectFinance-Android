package es.wokis.projectfinance.data.domain.category

import es.wokis.projectfinance.data.repository.category.CategoryRepository

interface InsertRemovedCategoryUseCase {
    suspend operator fun invoke()
}

class InsertRemovedCategoryUseCaseImpl(private val repository: CategoryRepository) : InsertRemovedCategoryUseCase {

    override suspend fun invoke() {
        repository.insertRemovedCategory()
    }

}