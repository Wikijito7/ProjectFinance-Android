package es.wokis.projectfinance.utils

import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.constants.AppConstants

fun showReinsertInvoiceSnackBar(view: View, action: () -> Unit) {
    showSnackBar(R.string.info__invoice_removed, R.string.general__revert, view, action = action)
}

fun showSnackBar(
    @StringRes title: Int,
    @StringRes actionText: Int = 0,
    view: View,
    snackBarDuration: Int = AppConstants.SNACK_BAR_DURATION,
    action: () -> Unit = { },
    onDismissed: () -> Unit = { }
) {
    try {
        val snackBar =
            Snackbar.make(view, title, snackBarDuration)
        if (actionText != 0) {
            snackBar.setAction(actionText) {
                action()
            }
        }
        snackBar.view.elevation = 100f
        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                onDismissed()
            }
        })
        snackBar.show()

    } catch (exc: Exception) {
        Log.e("SNACKBAR", "showSnackBar: ${exc.stackTraceToString()}")
    }
}