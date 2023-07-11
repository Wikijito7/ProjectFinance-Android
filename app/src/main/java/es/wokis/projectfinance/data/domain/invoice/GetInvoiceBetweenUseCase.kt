package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository
import kotlinx.coroutines.flow.Flow

interface GetInvoiceBetweenUseCase {
    suspend operator fun invoke(startDate: Long, endDate: Long): Flow<List<InvoiceBO>>
}

class GetInvoiceBetweenUseCaseImpl(private val repository: InvoiceRepository) :
    GetInvoiceBetweenUseCase {
    override suspend operator fun invoke(startDate: Long, endDate: Long): Flow<List<InvoiceBO>> =
        repository.getInvoicesBetween(startDate, endDate)
}