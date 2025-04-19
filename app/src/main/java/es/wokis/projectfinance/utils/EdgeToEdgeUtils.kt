package es.wokis.projectfinance.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding


fun View.applyEdgeToEdge(
    applyTopPadding: Boolean = false,
    applyBottomPadding: Boolean = false,
    applyLeftPadding: Boolean = false,
    applyRightPadding: Boolean = false
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(
            WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout()
        )
        // Apply the insets as a margin to the view. This solution sets
        // only the bottom, left, and right dimensions, but you can apply whichever
        // insets are appropriate to your layout. You can also update the view padding
        // if that's more appropriate.
        view.updatePadding(
            left = insets.left.takeIf { applyLeftPadding } ?: view.paddingLeft,
            top = insets.top.takeIf { applyTopPadding } ?: view.paddingTop,
            right = insets.right.takeIf { applyRightPadding } ?: view.paddingRight,
            bottom = insets.bottom.takeIf { applyBottomPadding } ?: view.paddingBottom,
        )

        // Return CONSUMED if you don't want the window insets to keep passing
        // down to descendant views.
        WindowInsetsCompat.CONSUMED
    }
}
