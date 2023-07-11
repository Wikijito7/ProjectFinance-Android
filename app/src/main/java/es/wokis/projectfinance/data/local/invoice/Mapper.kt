package es.wokis.projectfinance.data.local.invoice

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_CATEGORY_ID
import es.wokis.projectfinance.data.local.category.toBO
import es.wokis.projectfinance.data.local.invoice.dbo.FullInvoiceDBO
import es.wokis.projectfinance.data.local.invoice.dbo.InvoiceDBO
import es.wokis.projectfinance.data.local.reaction.toBO

fun InvoiceBO.toDBO() = InvoiceDBO(
    id = id,
    title = title,
    description = description,
    quantity = quantity,
    date = date,
    type = type.key,
    foreignCategoryId = category?.id ?: DEFAULT_CATEGORY_ID,
    userId = userId,
    serverId = serverId,
    synchronize = synchronize,
    updated = updated
)

fun List<InvoiceBO>.toDBO() = this.map { it.toDBO() }

fun InvoiceDBO.toBO() = InvoiceBO(
    id = id,
    title = title,
    description = description.orEmpty(),
    quantity = quantity,
    date = date,
    type = InvoiceType.getFromKey(type),
    userId = userId,
    serverId = serverId,
    category = null,
    synchronize = synchronize,
    updated = updated
)

fun FullInvoiceDBO.toBO() = InvoiceBO(
    id = invoiceWithReaction.invoice.id,
    title = invoiceWithReaction.invoice.title,
    description = invoiceWithReaction.invoice.description.orEmpty(),
    quantity = invoiceWithReaction.invoice.quantity,
    date = invoiceWithReaction.invoice.date,
    type = InvoiceType.getFromKey(invoiceWithReaction.invoice.type),
    category = category?.toBO(),
    userId = invoiceWithReaction.invoice.userId,
    serverId = invoiceWithReaction.invoice.serverId,
    synchronize = invoiceWithReaction.invoice.synchronize,
    updated = invoiceWithReaction.invoice.updated,
    reactions = invoiceWithReaction.reactions.toBO()
)

fun List<InvoiceDBO>?.toBO() = this?.map { it.toBO() }.orEmpty()

@JvmName("invoiceWithCategoryDBOToBO")
fun List<FullInvoiceDBO>?.toBO() = this?.map { it.toBO() }.orEmpty()