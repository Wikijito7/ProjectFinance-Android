package es.wokis.projectfinance.data.enums

enum class DetailOptionsEnum(val key: String) {
    EDIT_INVOICE("EDIT_INVOICE"),
    REMOVE_INVOICE("REMOVE_INVOICE"),
    ADD_REACTION("ADD_REACTION");

    companion object {
        fun getFromKey(key: String) = values().find { it.key == key }
    }
}