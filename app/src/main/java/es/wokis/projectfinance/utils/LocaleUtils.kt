package es.wokis.projectfinance.utils

import android.content.Context
import android.os.Build
import java.util.Locale

fun Context.getLocale(): Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    this.resources.configuration.locales.get(0)

} else {
    // This locale is deprecated from Android N.
    // As we are supporting Android L, we have to use it this way.
    this.resources.configuration.locale
}
