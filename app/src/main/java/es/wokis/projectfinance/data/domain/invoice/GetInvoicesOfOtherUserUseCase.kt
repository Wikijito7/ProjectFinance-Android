package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository

interface GetInvoicesOfOtherUserUseCase {
    suspend operator fun invoke(): List<InvoiceBO>
}

class GetInvoicesOfOtherUserUseCaseImpl(private val repository: InvoiceRepository) : GetInvoicesOfOtherUserUseCase {

    override suspend fun invoke(): List<InvoiceBO> = repository.getInvoicesOfOtherUser()

}
