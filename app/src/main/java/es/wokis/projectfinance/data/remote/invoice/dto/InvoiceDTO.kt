package es.wokis.projectfinance.data.remote.invoice.dto

import com.google.gson.annotations.SerializedName
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT

data class InvoiceDTO(
    @SerializedName("server_id")
    val serverId: String? = null,
    @SerializedName("id")
    val id: Long = 0L,
    @SerializedName("title")
    val title: String = EMPTY_TEXT,
    @SerializedName("description")
    val description: String = EMPTY_TEXT,
    @SerializedName("quantity")
    val quantity: Int = 0,
    @SerializedName("date")
    val date: Long = 0L,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("category")
    val category: CategoryDTO? = null,
    @SerializedName("reactions")
    val reactions: List<ReactionDTO> = emptyList()
)

data class CategoryDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("color")
    val color: String,
)

data class ReactionDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("unicode")
    val unicode: String
)