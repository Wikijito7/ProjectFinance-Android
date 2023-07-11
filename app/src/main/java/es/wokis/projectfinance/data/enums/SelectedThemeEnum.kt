package es.wokis.projectfinance.data.enums

enum class SelectedThemeEnum(val key: String) {
    LIGHT("LIGHT"),
    DARK("DARK"),
    SYSTEM("SYSTEM");

    companion object {
        fun fromKey(key: String?) = values().find { key == it.key } ?: SYSTEM
    }
}