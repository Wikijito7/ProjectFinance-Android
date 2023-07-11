package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository
import kotlinx.coroutines.flow.Flow

interface GetFilteredInvoiceUseCase {
    suspend operator fun invoke(): Flow<List<InvoiceBO>>
}

class GetFilteredInvoiceUseCaseImpl(private val invoiceRepository: InvoiceRepository) :
    GetFilteredInvoiceUseCase {
    override suspend fun invoke(): Flow<List<InvoiceBO>> = invoiceRepository.getFilteredInvoices()
}