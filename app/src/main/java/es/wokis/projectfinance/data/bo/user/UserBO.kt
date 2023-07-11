package es.wokis.projectfinance.data.bo.user

import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_LANG
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import java.util.Date

data class UserBO(
    val id: String,
    val username: String,
    val email: String,
    val image: String = EMPTY_TEXT,
    val lang: String = DEFAULT_LANG,
    val createdOn: Date,
    val totpEnabled: Boolean,
    val emailVerified: Boolean = false,
    val loginWithGoogle: Boolean = false,
    val devices: List<String> = emptyList(),
    val badges: List<BadgeBO> = emptyList(),
)

data class BadgeBO(
    val id: Int,
    val color: String
)

enum class BadgeType(val id: Int) {
    EMAIL_VERIFIED(1),
    BETA_TESTER(2),
    LEGACY(3);

    companion object {
        fun fromKey(id: Int) = values().find { it.id == id }
    }
}
