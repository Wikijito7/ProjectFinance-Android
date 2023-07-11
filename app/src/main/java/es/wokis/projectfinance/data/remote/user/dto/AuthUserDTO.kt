package es.wokis.projectfinance.data.remote.user.dto

import com.google.gson.annotations.SerializedName
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_LANG

data class AuthUserDTO(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("lang")
    val lang: String = DEFAULT_LANG,
)