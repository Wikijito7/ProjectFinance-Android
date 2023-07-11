package es.wokis.projectfinance.ui.home.vo

import es.wokis.projectfinance.data.bo.invoice.InvoiceType

data class HomeInvoiceVO(
    val id: Long,
    val title: String,
    val date: String,
    val quantity: Float,
    val type: InvoiceType
)