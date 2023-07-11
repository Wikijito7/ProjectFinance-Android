package es.wokis.projectfinance.utils

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import es.wokis.projectfinance.BuildConfig
import es.wokis.projectfinance.R

private val builder = NavOptions.Builder()
    .setEnterAnim(R.anim.fragment_fade_in)
    .setExitAnim(R.anim.fragment_fade_out)
    .setPopEnterAnim(R.anim.fragment_fade_in)
    .setPopExitAnim(R.anim.fragment_fade_out)
    .build()

fun NavController.safeNavigation(direction: NavDirections) {
    try {
        navigate(direction, builder)

    } catch (exc: Throwable) {
        Log.e(BuildConfig.APPLICATION_ID, exc.stackTraceToString())
    }
}

fun NavController.safeNavigation(direction: Int) {
    try {
        navigate(direction, null, builder)

    } catch (exc: Throwable) {
        Log.e(BuildConfig.APPLICATION_ID, exc.stackTraceToString())
    }
}