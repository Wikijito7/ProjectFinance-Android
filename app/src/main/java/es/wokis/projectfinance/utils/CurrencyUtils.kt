package es.wokis.projectfinance.utils

import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun Int.toMonetaryFloat(): Float = (this.toFloat() / 100f)

fun Float.toMonetaryFloat(): Float = this / 100f

fun Double.toMonetaryDouble(): Double = (this / 100)

fun Float.toCents(): Int = (this * 100).toInt()

fun Float.asCurrency(locale: Locale): String {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    currencyFormat.maximumFractionDigits = 2
    currencyFormat.currency = try {
        Currency.getInstance(locale)

    } catch (exc: Exception) {
        // In case the locale has no currency, we default to US currency
        Currency.getInstance(Locale.US)
    }
    return currencyFormat.format(this)
}

fun Double.asCurrency(locale: Locale): String {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    currencyFormat.maximumFractionDigits = 2
    currencyFormat.currency = try {
        Currency.getInstance(locale)

    } catch (exc: Exception) {
        // In case the locale has no currency, we default to US currency
        Currency.getInstance(Locale.US)
    }
    return currencyFormat.format(this)
}

fun Double.asPercentage(): String = String.format("%.2f%%", this)

fun Int.withSign(invoiceType: InvoiceType): Int =
    this * if (invoiceType == InvoiceType.DEPOSIT) 1 else -1