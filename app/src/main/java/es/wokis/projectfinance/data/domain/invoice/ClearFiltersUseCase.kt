package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository

interface ClearFiltersUseCase {
    operator fun invoke()
}

class ClearFiltersUseCaseImpl(private val repository: InvoiceRepository) : ClearFiltersUseCase {
    override fun invoke() {
        repository.clearFilters()
    }

}
