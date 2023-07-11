package es.wokis.projectfinance.data.remote.user.dto

import com.google.gson.annotations.SerializedName

data class ChangePassRequestDTO(
    @SerializedName("oldPass")
    val oldPass: String?,
    @SerializedName("recoverCode")
    val recoverCode: String?,
    @SerializedName("newPass")
    val newPass: String
)