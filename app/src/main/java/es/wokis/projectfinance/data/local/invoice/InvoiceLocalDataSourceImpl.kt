package es.wokis.projectfinance.data.local.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.dao.invoice.InvoiceDAO
import es.wokis.projectfinance.data.datasource.invoice.InvoiceLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvoiceLocalDataSourceImpl @Inject constructor(private val dao: InvoiceDAO) :
    InvoiceLocalDataSource {

    override suspend fun getInvoices(userId: String): Flow<List<InvoiceBO>> =
        dao.getAllInvoices(userId).map { it.toBO() }

    override suspend fun getNumberOfInvoices(userId: String): Flow<Int> =
        dao.getNumberOfInvoices(userId)

    override suspend fun addInvoice(invoice: InvoiceBO): Boolean =
        dao.addInvoice(invoice.toDBO()) > 0

    override suspend fun addInvoices(invoices: List<InvoiceBO>): Boolean =
        dao.addInvoices(invoices.toDBO()).isNotEmpty()

    override suspend fun removeInvoice(invoiceId: Long): Boolean =
        dao.removeInvoice(invoiceId) > 0

    override suspend fun updateInvoice(invoice: InvoiceBO): Boolean =
        dao.updateInvoice(invoice.toDBO()) > 0

    override suspend fun getInvoicesNotSynchronized(userId: String): List<InvoiceBO> =
        dao.getInvoicesNotSynchronized(userId).toBO()

    override suspend fun getInvoicesUpdatedNotSynchronized(userId: String): List<InvoiceBO> =
        dao.getInvoicesUpdatedNotSynchronized(userId).toBO()

    override suspend fun getLastInvoices(
        userId: String,
        numberOfInvoices: Int
    ): Flow<List<InvoiceBO>> =
        dao.getInvoices(userId, numberOfInvoices).map { it.toBO() }

    override suspend fun getInvoiceById(id: Long): InvoiceBO = dao.getInvoiceById(id).toBO()

    override suspend fun getInvoicesBetween(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<InvoiceBO>> =
        dao.getInvoicesBetween(userId, startDate, endDate).map { it.toBO() }

    override suspend fun getInvoicesOn(date: Long): Flow<List<InvoiceBO>> =
        dao.getInvoicesOn(date).map { it.toBO() }

    override suspend fun getInvoicesWithoutCategory(): List<InvoiceBO> =
        dao.getInvoicesWithoutCategory().toBO()

    override suspend fun getInvoicesOfOtherUser(userId: String): List<InvoiceBO> =
        dao.getInvoicesOfOtherUser(userId).toBO()

}