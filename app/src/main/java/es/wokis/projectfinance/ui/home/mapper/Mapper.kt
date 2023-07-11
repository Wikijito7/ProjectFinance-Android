package es.wokis.projectfinance.ui.home.mapper

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.ui.home.vo.HomeInvoiceVO
import es.wokis.projectfinance.utils.toMonetaryFloat
import es.wokis.projectfinance.utils.toStringFormatted

fun InvoiceBO.toHomeVO() = HomeInvoiceVO(
    id = id,
    title = title,
    date = date.toStringFormatted(),
    quantity = quantity.toMonetaryFloat(),
    type = type
)

fun List<InvoiceBO>.toHomeVO() = this.map { it.toHomeVO() }