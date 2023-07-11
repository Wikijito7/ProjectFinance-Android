package es.wokis.projectfinance.data.remote.user.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserDTO(
    @SerializedName("username")
    val username: String?,
    @SerializedName("email")
    val email: String?,
)