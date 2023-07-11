package es.wokis.projectfinance.data.local.category.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryDBO(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long,
    val categoryName: String,
    val color: String
)