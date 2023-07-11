package es.wokis.projectfinance.data.bo.totp

data class TOTPResponseBO(
    val encodedSecret: String,
    val totpUrl: String,
    val words: List<String>
)