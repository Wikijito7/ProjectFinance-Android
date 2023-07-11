package es.wokis.projectfinance.utils

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt

fun Float?.orZero() = this ?: 0f

fun Double?.orZero(): Double = this ?: 0.0

fun Int?.orZero() = this ?: 0

fun Long?.orZero() = this ?: 0L

fun Int.asDp(resources: Resources) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(), resources.displayMetrics
    ).roundToInt()