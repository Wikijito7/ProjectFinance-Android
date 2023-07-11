package es.wokis.projectfinance.utils

import android.content.Context
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.constants.AppConstants

fun Context?.getCategoryName(categoryName: String?): String = this?.let {
    categoryName.let {
        when (it) {
            AppConstants.DEFAULT_CATEGORY -> getString(R.string.category__none)
            else -> it
        }
    }
} ?: categoryName.orEmpty()