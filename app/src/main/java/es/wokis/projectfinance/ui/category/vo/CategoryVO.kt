package es.wokis.projectfinance.ui.category.vo

data class CategoryVO(
    val id: Long,
    val title: String,
    val color: String,
    val selected: Boolean
)