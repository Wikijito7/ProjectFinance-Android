package es.wokis.projectfinance.ui.dashboard.mapper

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_CATEGORY
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_COLOR
import es.wokis.projectfinance.ui.dashboard.vo.InvoiceVO
import es.wokis.projectfinance.utils.toMonetaryFloat
import es.wokis.projectfinance.utils.toStringFormatted

fun InvoiceBO.toVO() = InvoiceVO(
    id = id,
    title = title,
    description = description,
    quantity = quantity.toMonetaryFloat(),
    date = date.toStringFormatted(),
    categoryName = category?.title.orNone(),
    categoryColor = category?.color.orDefault(),
    type = type,
    cloudSync = serverId.isNullOrBlank().not(),
    synchronize = synchronize,
    updated = updated,
    reactions = reactions.map { it.unicode }
)

fun List<InvoiceBO>.toVO() = this.map { it.toVO() }

private fun String?.orNone() = this ?: DEFAULT_CATEGORY

private fun String?.orDefault() = this ?: DEFAULT_COLOR