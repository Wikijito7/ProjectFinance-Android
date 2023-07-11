package es.wokis.projectfinance.ui.category.mapper

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.ui.category.vo.CategoryVO

fun CategoryBO.toVO(categoryId: Long = 0L) = CategoryVO(
    id,
    title,
    color,
    categoryId == id
)

fun CategoryVO.toBO() = CategoryBO(
    id,
    title,
    color
)

fun List<CategoryBO>.toVO(categoryId: Long = 0L) = this.map { it.toVO(categoryId) }