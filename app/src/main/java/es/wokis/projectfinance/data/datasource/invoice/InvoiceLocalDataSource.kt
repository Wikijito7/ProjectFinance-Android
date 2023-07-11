package es.wokis.projectfinance.data.datasource.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import kotlinx.coroutines.flow.Flow

interface InvoiceLocalDataSource {
    suspend fun getInvoices(userId: String): Flow<List<InvoiceBO>>
    suspend fun getNumberOfInvoices(userId: String): Flow<Int>
    suspend fun addInvoice(invoice: InvoiceBO): Boolean
    suspend fun addInvoices(invoices: List<InvoiceBO>): Boolean
    suspend fun getLastInvoices(userId: String, numberOfInvoices: Int): Flow<List<InvoiceBO>>
    suspend fun getInvoiceById(id: Long): InvoiceBO
    suspend fun getInvoicesBetween(userId: String, startDate: Long, endDate: Long): Flow<List<InvoiceBO>>
    suspend fun getInvoicesOn(date: Long): Flow<List<InvoiceBO>>
    suspend fun getInvoicesWithoutCategory(): List<InvoiceBO>
    suspend fun removeInvoice(invoiceId: Long): Boolean
    suspend fun updateInvoice(invoice: InvoiceBO): Boolean
    suspend fun getInvoicesNotSynchronized(userId: String): List<InvoiceBO>
    suspend fun getInvoicesUpdatedNotSynchronized(userId: String): List<InvoiceBO>
    suspend fun getInvoicesOfOtherUser(userId: String): List<InvoiceBO>
}