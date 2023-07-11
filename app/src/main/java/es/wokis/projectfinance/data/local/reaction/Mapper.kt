package es.wokis.projectfinance.data.local.reaction

import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.data.local.reaction.dbo.ReactionDBO

fun ReactionBO.toDBO() = ReactionDBO(
    id = id,
    unicode = unicode,
    invoiceId = invoiceId
)

fun List<ReactionDBO>?.toBO(): List<ReactionBO> = this?.map { it.toBO() }.orEmpty()

fun ReactionDBO.toBO(): ReactionBO = ReactionBO(
    id = id,
    unicode = unicode,
    invoiceId = invoiceId
)