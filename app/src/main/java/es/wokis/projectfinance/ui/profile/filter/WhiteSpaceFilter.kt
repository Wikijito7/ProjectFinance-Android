package es.wokis.projectfinance.ui.profile.filter

import android.text.InputFilter
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT

val whiteSpaceFilter = InputFilter { source, start, end, _, _, _ ->
    for (i in start until end) {
        if (Character.isSpaceChar(source[i])) {
            return@InputFilter EMPTY_TEXT
        }
    }
    null
}