package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository
import kotlinx.coroutines.flow.Flow

interface GetInvoiceUseCase {
    suspend operator fun invoke(): Flow<List<InvoiceBO>>
}

class GetInvoiceUseCaseImpl(private val repository: InvoiceRepository) : GetInvoiceUseCase {
    override suspend operator fun invoke(): Flow<List<InvoiceBO>> = repository.getInvoices()
}