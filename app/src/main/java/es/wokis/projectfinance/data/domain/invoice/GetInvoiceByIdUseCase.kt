package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository

interface GetInvoiceByIdUseCase {
    suspend operator fun invoke(id: Long): InvoiceBO
}

class GetInvoiceByIdUseCaseImpl(private val repository: InvoiceRepository) : GetInvoiceByIdUseCase {
    override suspend operator fun invoke(id: Long): InvoiceBO = repository.getInvoiceById(id)
}