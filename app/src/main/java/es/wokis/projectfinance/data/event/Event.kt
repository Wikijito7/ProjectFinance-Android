package es.wokis.projectfinance.data.event

class Event<T>(private val data: T?) {
    private var handled = false

    fun getDataIfNotHandled(): T? = data?.takeIf { handled.not() }?.also {
        handled = true
    }
}