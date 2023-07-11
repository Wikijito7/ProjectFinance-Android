package es.wokis.projectfinance.data.local.invoice.dbo

import androidx.room.Embedded
import es.wokis.projectfinance.data.local.category.dbo.CategoryDBO
import es.wokis.projectfinance.data.local.invoicewithreaction.dbo.InvoicesWithReactionsDBO

data class FullInvoiceDBO(
    @Embedded val invoiceWithReaction: InvoicesWithReactionsDBO,
    @Embedded val category: CategoryDBO?
)
