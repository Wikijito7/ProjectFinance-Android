package es.wokis.projectfinance.data.remote.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.datasource.invoice.InvoiceRemoteDataSource
import es.wokis.projectfinance.data.remote.invoice.mapper.toBO
import es.wokis.projectfinance.data.remote.invoice.mapper.toDTO
import es.wokis.projectfinance.data.remote.invoice.service.InvoiceService

class InvoiceRemoteDataSourceImpl(private val service: InvoiceService) : InvoiceRemoteDataSource {

    override suspend fun getInvoices(): List<InvoiceBO> = service.getInvoices().toBO()

    override suspend fun addInvoices(invoices: List<InvoiceBO>): Boolean =
        service.addInvoices(invoices.toDTO()).acknowledge

    override suspend fun updateInvoices(invoices: List<InvoiceBO>): Boolean =
        service.updateInvoices(invoices.toDTO()).acknowledge

    override suspend fun deleteInvoices(invoices: List<String>): Boolean =
        service.deleteInvoices(invoices).acknowledge

}