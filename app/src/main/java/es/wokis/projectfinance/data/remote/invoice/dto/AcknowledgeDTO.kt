package es.wokis.projectfinance.data.remote.invoice.dto

import com.google.gson.annotations.SerializedName

data class AcknowledgeDTO(
    @SerializedName("acknowledge")
    val acknowledge: Boolean
)