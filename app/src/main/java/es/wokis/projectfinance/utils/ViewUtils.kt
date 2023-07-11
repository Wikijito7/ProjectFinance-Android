package es.wokis.projectfinance.utils

import android.content.res.Resources
import android.graphics.Paint
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.isVisible
import es.wokis.projectfinance.R
import kotlin.math.roundToInt


fun View?.hide() {
    this?.visibility = View.GONE
}

fun View?.show() {
    this?.visibility = View.VISIBLE
}

fun View?.setVisible(condition: Boolean, block: () -> Unit = { }) {
    if (condition) {
        this.show()
        block()

    } else {
        this.hide()
    }
}

fun View?.showAnim() {
    this?.let {
        if (!isVisible) {
            startAnimation(getShowAnimation())
        }
        show()
    }
}

fun View?.hideAnim() {
    this?.let {
        if (isVisible) {
            startAnimation(getHideAnimation())
        }
        hide()
    }
}

fun View?.isShown() = this?.visibility == View.VISIBLE

fun Int.toDp(): Int = (this * Resources.getSystem().displayMetrics.density).roundToInt()

fun TextView.underLineText() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

private fun View.getShowAnimation() = AnimationUtils.loadAnimation(context, R.anim.slide_left)

private fun View.getHideAnimation() = AnimationUtils.loadAnimation(context, R.anim.slide_right)

private fun View.onAnimationEnd(block: () -> Unit) {
    animation.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
            // no-op
        }

        override fun onAnimationEnd(p0: Animation?) {
            block()
        }

        override fun onAnimationRepeat(p0: Animation?) {
            // no-op
        }

    })
}