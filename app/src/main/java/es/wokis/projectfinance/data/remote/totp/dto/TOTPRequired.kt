package es.wokis.projectfinance.data.remote.totp.dto

import com.google.gson.annotations.SerializedName

data class TOTPRequestDTO(
    @SerializedName("authType")
    val authType: String,
    @SerializedName("timestamp")
    val timestamp: Long,
)