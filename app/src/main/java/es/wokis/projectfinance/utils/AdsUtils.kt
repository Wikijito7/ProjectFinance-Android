package es.wokis.projectfinance.utils

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceType

private const val INVOICES_PER_AD = 6

fun List<InvoiceBO>.applyAdsToList() = filter { invoice ->
    invoice.type != InvoiceType.AD
}.toMutableList().apply {
    val adsQuantity = size / INVOICES_PER_AD
    for (x in 1..adsQuantity) {
        val offset = x - 1
        val position = (x * INVOICES_PER_AD) + offset
        val adRow = InvoiceBO(type = InvoiceType.AD)
        add(position, adRow)
    }
}.toList()