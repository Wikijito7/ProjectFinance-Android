package es.wokis.projectfinance.data.remote.invoice.mapper

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.data.remote.invoice.dto.CategoryDTO
import es.wokis.projectfinance.data.remote.invoice.dto.InvoiceDTO
import es.wokis.projectfinance.data.remote.invoice.dto.ReactionDTO
import java.util.*

fun InvoiceDTO.toBO() = InvoiceBO(
    serverId = serverId,
    id = id,
    title = title,
    description = description,
    quantity = quantity,
    date = Date(date),
    type = InvoiceType.getFromKey(type.orEmpty()),
    reactions = reactions.toBO(id),
    userId = userId.orEmpty(),
    category = category?.toBO(),
)

fun CategoryDTO.toBO() = CategoryBO(
    id = id,
    title = title,
    color = color
)

fun ReactionDTO.toBO(invoiceId: Long) = ReactionBO(
    id = id,
    unicode = unicode,
    invoiceId = invoiceId
)

@JvmName("invoiceDTOToBO")
fun List<InvoiceDTO>.toBO() = this.map { it.toBO() }

fun List<InvoiceBO>.toDTO(): List<InvoiceDTO> = this.map { it.toDTO() }

@JvmName("reactionDTOToBO")
fun List<ReactionDTO>.toBO(invoiceId: Long): List<ReactionBO> = this.map { it.toBO(invoiceId) }

@JvmName("reactionBOToDTO")
fun List<ReactionBO>.toDTO(): List<ReactionDTO> = this.map { it.toDTO() }

private fun ReactionBO.toDTO() = ReactionDTO(
    id = id,
    unicode = unicode
)

fun InvoiceBO.toDTO() = InvoiceDTO(
    serverId = serverId,
    id = id,
    title = title,
    description = description,
    quantity = quantity,
    date = date.time,
    type = type.key,
    userId = userId,
    reactions = reactions.toDTO(),
    category = category?.toDTO()
)

fun CategoryBO.toDTO() = CategoryDTO(
    id = id,
    title = title,
    color = color
)