package es.wokis.projectfinance.data.bo.invoice

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import java.util.Date

data class InvoiceBO(
    val id: Long = 0L,
    val title: String = EMPTY_TEXT,
    val description: String = EMPTY_TEXT,
    val quantity: Int = 0,
    val date: Date = Date(),
    val type: InvoiceType,
    val category: CategoryBO? = null,
    val reactions: List<ReactionBO> = emptyList(),
    val userId: String? = null,
    val serverId: String? = null,
    val synchronize: Boolean = true,
    val updated: Boolean = false
)

enum class InvoiceType(val key: String) {
    DEPOSIT("DEPOSIT"),
    EXPENSE("EXPENSE"),
    AD("AD");

    companion object {
        fun getFromKey(key: String) = values().find { it.key == key } ?: DEPOSIT
    }
}
