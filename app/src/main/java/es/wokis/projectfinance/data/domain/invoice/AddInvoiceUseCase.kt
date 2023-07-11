package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository

interface AddInvoiceUseCase {
    suspend operator fun invoke(invoice: InvoiceBO): Boolean
}

class AddInvoiceUseCaseImpl(private val repository: InvoiceRepository) : AddInvoiceUseCase {
    override suspend operator fun invoke(invoice: InvoiceBO): Boolean =
        repository.addInvoice(invoice)
}