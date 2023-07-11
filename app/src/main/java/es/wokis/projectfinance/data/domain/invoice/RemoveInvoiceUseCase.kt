package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository

interface RemoveInvoiceUseCase {
    suspend operator fun invoke(invoice: InvoiceBO, synchronize: Boolean = true): Boolean
}

class RemoveInvoiceUseCaseImpl(private val repository: InvoiceRepository) : RemoveInvoiceUseCase {
    override suspend operator fun invoke(invoice: InvoiceBO, synchronize: Boolean): Boolean =
        repository.removeInvoice(invoice, synchronize)
}