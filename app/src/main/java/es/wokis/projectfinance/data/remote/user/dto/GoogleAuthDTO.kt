package es.wokis.projectfinance.data.remote.user.dto

import com.google.gson.annotations.SerializedName

data class GoogleAuthDTO (
    @SerializedName("authToken")
    val authToken: String
)