package es.wokis.projectfinance.data.local.reaction.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reaction")
data class ReactionDBO(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val unicode: String,
    val invoiceId: Long
)