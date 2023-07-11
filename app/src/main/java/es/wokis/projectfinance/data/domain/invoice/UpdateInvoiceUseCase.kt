package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository

interface UpdateInvoiceUseCase {
    suspend operator fun invoke(invoice: InvoiceBO): Boolean
}

class UpdateInvoiceUseCaseImpl(private val repository: InvoiceRepository) : UpdateInvoiceUseCase {
    override suspend operator fun invoke(invoice: InvoiceBO): Boolean =
        repository.updateInvoice(invoice)
}