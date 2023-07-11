package es.wokis.projectfinance.data.domain.category

import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository

interface CheckInvoicesWithoutCategoryUseCase {
    suspend operator fun invoke()
}

class CheckInvoicesWithoutCategoryUseCaseImpl(private val invoiceRepository: InvoiceRepository) :
    CheckInvoicesWithoutCategoryUseCase {

    override suspend fun invoke() {
        invoiceRepository.checkInvoicesWithoutCategory()
    }

}
