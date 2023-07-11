package es.wokis.projectfinance.data.bo.totp

data class TOTPRequiredBO(
    val authType: String,
    val timestamp: Long
)