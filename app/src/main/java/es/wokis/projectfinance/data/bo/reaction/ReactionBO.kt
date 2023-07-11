package es.wokis.projectfinance.data.bo.reaction

data class ReactionBO(
    val id: Long = 0,
    val unicode: String,
    val invoiceId: Long
)