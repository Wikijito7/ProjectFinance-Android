package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface SynchronizeInvoicesUseCase {
    suspend operator fun invoke(): Flow<AsyncResult<Boolean>>
}

class SynchronizeInvoicesUseCaseImpl(private val repository: InvoiceRepository) : SynchronizeInvoicesUseCase {

    override suspend fun invoke(): Flow<AsyncResult<Boolean>> = repository.synchronizeInvoices()

}