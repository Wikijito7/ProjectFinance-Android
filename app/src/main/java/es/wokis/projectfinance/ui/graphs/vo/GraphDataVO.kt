package es.wokis.projectfinance.ui.graphs.vo

data class GraphDataVO(
    val title: String,
    val amount: Double,
    val description: String? = null,
    val categoryColor: String? = null
)