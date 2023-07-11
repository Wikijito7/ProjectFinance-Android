package es.wokis.projectfinance.data.domain.invoice

import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository
import kotlinx.coroutines.flow.Flow

interface GetNumberOfInvoiceUseCase {
    suspend operator fun invoke(): Flow<Int>
}

class GetNumberOfInvoiceUseCaseImpl(private val repository: InvoiceRepository) :
    GetNumberOfInvoiceUseCase {

    override suspend operator fun invoke(): Flow<Int> = repository.getNumberOfInvoices()

}