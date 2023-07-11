package es.wokis.projectfinance.data.remote.user.dto

import com.google.gson.annotations.SerializedName
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT

data class UserDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("image")
    val image: String = EMPTY_TEXT,
    @SerializedName("lang")
    val lang: String,
    @SerializedName("createdOn")
    val createdOn: Long,
    @SerializedName("totpEnabled")
    val totpEnabled: Boolean,
    @SerializedName("emailVerified")
    val emailVerified: Boolean = false,
    @SerializedName("loginWithGoogle")
    val loginWithGoogle: Boolean = false,
    @SerializedName("devices")
    val devices: List<String> = emptyList(),
    @SerializedName("badges")
    val badges: List<BadgeDTO> = emptyList(),
)

data class BadgeDTO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("color")
    val color: String
)
