package es.wokis.projectfinance.utils

import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import androidx.core.graphics.ColorUtils

fun getTintByContrast(color: Int): Int {
    val white = Color.WHITE
    val black = Color.BLACK
    val contrast = ColorUtils.calculateContrast(white, color)
    return if (contrast > 1.5) white else black
}

fun Resources.Theme?.getPrimaryColor(): Int? = this?.let {
    val typedValue = TypedValue()
    it.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
    return typedValue.data
}

fun Resources.Theme?.textColorPrimary(): Int? = this?.let {
    val typedValue = TypedValue()
    it.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
    return typedValue.data
}