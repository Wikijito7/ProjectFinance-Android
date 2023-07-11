package es.wokis.projectfinance.data.local.invoicewithreaction.dbo

import androidx.room.Embedded
import androidx.room.Relation
import es.wokis.projectfinance.data.local.invoice.dbo.InvoiceDBO
import es.wokis.projectfinance.data.local.reaction.dbo.ReactionDBO

data class InvoicesWithReactionsDBO(
    @Embedded val invoice: InvoiceDBO,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val reactions: List<ReactionDBO>?
)