package es.wokis.projectfinance.data.bo.user

data class AuthUserBO(
    val username: String,
    val password: String,
    val email: String? = null,
    val lang: String
)