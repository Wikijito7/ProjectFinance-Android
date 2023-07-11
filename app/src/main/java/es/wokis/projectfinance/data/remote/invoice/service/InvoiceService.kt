package es.wokis.projectfinance.data.remote.invoice.service

import es.wokis.projectfinance.data.remote.invoice.dto.AcknowledgeDTO
import es.wokis.projectfinance.data.remote.invoice.dto.InvoiceDTO
import retrofit2.http.*

interface InvoiceService {
    @GET("/api/invoices")
    suspend fun getInvoices(): List<InvoiceDTO>

    @POST("/api/invoices")
    suspend fun addInvoices(@Body invoices: List<InvoiceDTO>): AcknowledgeDTO

    @PUT("/api/invoices")
    suspend fun updateInvoices(@Body invoices: List<InvoiceDTO>): AcknowledgeDTO

    @HTTP(method = "DELETE", path = "/api/invoices", hasBody = true)
    suspend fun deleteInvoices(@Body invoices: List<String>): AcknowledgeDTO
}