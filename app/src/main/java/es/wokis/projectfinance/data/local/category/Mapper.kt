package es.wokis.projectfinance.data.local.category

import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.local.category.dbo.CategoryDBO

fun CategoryBO.toDBO() = CategoryDBO(id, title, color)

fun List<CategoryBO>.toDBO() = this.map { it.toDBO() }

fun CategoryDBO.toBO() = CategoryBO(categoryId, categoryName, color.orEmpty())

fun List<CategoryDBO>?.toBO() = this?.map { it.toBO() }.orEmpty()