package es.wokis.projectfinance.utils

import android.graphics.drawable.Drawable

data class SwipeButton(
    val color: Int,
    val drawable: Drawable?,
    val event: (position: Int) -> Unit
)
