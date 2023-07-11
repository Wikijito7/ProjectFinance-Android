package es.wokis.projectfinance.ui.dashboard.vo

import es.wokis.projectfinance.data.bo.invoice.InvoiceType

data class InvoiceVO(
    val id: Long,
    val title: String,
    val description: String,
    val quantity: Float,
    val date: String,
    val categoryName: String,
    val categoryColor: String,
    val type: InvoiceType,
    val cloudSync: Boolean,
    val synchronize: Boolean,
    val updated: Boolean,
    val reactions: List<String>
)