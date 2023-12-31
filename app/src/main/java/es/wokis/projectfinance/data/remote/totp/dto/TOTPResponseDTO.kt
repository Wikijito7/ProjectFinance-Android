package es.wokis.projectfinance.data.remote.totp.dto

import com.google.gson.annotations.SerializedName

data class TOTPResponseDTO(
    @SerializedName("encodedSecret")
    val encodedSecret: String,
    @SerializedName("totpUrl")
    val totpUrl: String,
    @SerializedName("words")
    val words: List<String>
)