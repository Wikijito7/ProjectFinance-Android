package es.wokis.projectfinance.data.constants

object AppConstants {
    const val EMPTY_TEXT = ""
    const val WHITE_SPACE = " "
    const val NEW_LINE = "\n"
    const val INSERTED = "INSERTED"
    const val SNACK_BAR_DURATION = 5000
    const val DEFAULT_CATEGORY = "NONE"
    const val DEFAULT_CATEGORY_ID = 1L
    const val DEFAULT_COLOR = "#FFAABB"
    const val FILTER_CATEGORIES_NAME = "FILTER_CATEGORIES_NAME"
    const val FILTER_CATEGORIES_ID = "FILTER_CATEGORIES_ID"
    const val FILTER_DATE_FROM = "FILTER_DATE_FROM"
    const val FILTER_DATE_TO = "FILTER_DATE_TO"
    const val CATEGORIES_SEPARATOR = ", "
    const val DEFAULT_LANG = "en"

    // region shared preferences
    const val SHARED_PREFERENCES_NAME = "project-finance_shared-preferences"
    const val USER_BEARER_TOKEN = "USER_BEARER_TOKEN"
    const val USER_ID = "USER_ID"
    const val SHOW_SWIPE_TUTORIAL = "SHOW_SWIPE_TUTORIAL"
    const val SHOW_REACTION_TUTORIAL = "SHOW_REACTION_TUTORIAL"
    const val SHOW_CHARTS_TUTORIAL = "SHOW_CHARTS_TUTORIAL"
    const val BIOMETRICS_ENABLED = "BIOMETRICS_ENABLED"
    const val OTP_ENABLED = "OTP_ENABLED"
    const val SECURE_MODE_ENABLED = "SECURE_MODE_ENABLED"
    const val SELECTED_THEME = "SELECTED_THEME"
    const val TOTP = "TOTP"
    const val TOTP_TIMESTAMP = "TOTP_TIMESTAMP"
    const val RESEND_EMAIL_TIMESTAMP = "RESEND_EMAIL_TIMESTAMP"
    // endregion

    const val TOTP_HEADER = "2FA"
    const val TIMESTAMP_HEADER = "timestamp"
}