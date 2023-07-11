package es.wokis.projectfinance.data.bo.user

data class ChangePassRequestBO(
    val oldPass: String? = null,
    val recoverCode: String? = null,
    val newPass: String
)