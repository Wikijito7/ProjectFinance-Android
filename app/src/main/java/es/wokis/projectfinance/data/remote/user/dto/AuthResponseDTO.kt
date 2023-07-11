package es.wokis.projectfinance.data.remote.user.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDTO(
    @SerializedName("authToken")
    val authToken: String
)