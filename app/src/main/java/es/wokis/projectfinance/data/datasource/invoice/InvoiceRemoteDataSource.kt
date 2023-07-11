package es.wokis.projectfinance.data.datasource.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO

interface InvoiceRemoteDataSource {
    suspend fun getInvoices(): List<InvoiceBO>
    suspend fun addInvoices(invoices: List<InvoiceBO>): Boolean
    suspend fun updateInvoices(invoices: List<InvoiceBO>): Boolean
    suspend fun deleteInvoices(invoices: List<String>): Boolean
}