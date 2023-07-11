package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository

interface InsertRemovedInvoiceUseCase {
    suspend operator fun invoke(): Boolean
}

class InsertRemovedInvoiceUseCaseImpl(private val repository: InvoiceRepository) : InsertRemovedInvoiceUseCase {
    override suspend operator fun invoke(): Boolean = repository.reinsertRemovedInvoice()
}