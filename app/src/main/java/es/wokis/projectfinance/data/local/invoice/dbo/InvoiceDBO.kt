package es.wokis.projectfinance.data.local.invoice.dbo

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.INTEGER
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import es.wokis.projectfinance.data.local.invoice.dbo.converter.DateConverter
import java.util.*

@Entity(tableName = "invoice")
@TypeConverters(DateConverter::class)
data class InvoiceDBO(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val description: String?,
    val quantity: Int,
    val date: Date,
    val type: String,
    val foreignCategoryId: Long,
    val userId: String? = null,
    val serverId: String? = null,
    @ColumnInfo(name = "synchronize", defaultValue = "1", typeAffinity = INTEGER)
    val synchronize: Boolean = true,
    @ColumnInfo(name = "updated", defaultValue = "0", typeAffinity = INTEGER)
    val updated: Boolean = false
)