package es.wokis.projectfinance.data.remote.user.dto

import com.google.gson.annotations.SerializedName

data class RecoverPassRequestDTO(
    @SerializedName("email")
    val email: String
)