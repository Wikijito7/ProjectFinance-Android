package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository
import kotlinx.coroutines.flow.Flow

interface GetLastInvoicesUseCase {
    suspend operator fun invoke(numberOfInvoices: Int): Flow<List<InvoiceBO>>
}

class GetLastInvoicesUseCaseImpl(private val repository: InvoiceRepository) :
    GetLastInvoicesUseCase {
    override suspend operator fun invoke(numberOfInvoices: Int): Flow<List<InvoiceBO>> =
        repository.getLastInvoices(numberOfInvoices)
}