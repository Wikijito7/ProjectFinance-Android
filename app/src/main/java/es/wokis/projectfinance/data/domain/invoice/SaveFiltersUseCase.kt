package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository

interface SaveFiltersUseCase {
    operator fun invoke(filters: Map<String, String?>)
}

class SaveFiltersUseCaseImpl(private val repository: InvoiceRepository) : SaveFiltersUseCase {
    override fun invoke(filters: Map<String, String?>) {
        repository.saveFilters(filters)
    }

}
