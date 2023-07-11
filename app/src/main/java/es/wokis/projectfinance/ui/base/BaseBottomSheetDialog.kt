package es.wokis.projectfinance.ui.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams
import androidx.annotation.StringRes
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.R
import es.wokis.projectfinance.ui.MainActivity
import es.wokis.projectfinance.utils.safeNavigation

@AndroidEntryPoint
abstract class BaseBottomSheetDialog : BottomSheetDialogFragment() {
    protected fun setUpDialog() {
        context?.let {
            dialog?.window?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    setDecorFitsSystemWindows(false)
                    setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN)

                } else {
                    setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    navigationBarColor = it.getColor(R.color.transparent)

                } else {
                    addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extendView()
    }

    fun extendView() {
        (dialog as? BottomSheetDialog)?.behavior?.peekHeight = Int.MAX_VALUE
    }

    fun forceExtendView() {
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            peekHeight = Int.MAX_VALUE
            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    state = BottomSheetBehavior.STATE_EXPANDED
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    state = BottomSheetBehavior.STATE_EXPANDED
                }

            })
        }
    }

    fun navigateTo(direction: NavDirections) {
        findNavController().safeNavigation(direction)
    }

    fun navigateBack() {
        findNavController().navigateUp()
    }

    fun showSnackBarMessage(@StringRes message: Int, duration: Int = 5000) {
        (activity as? MainActivity)?.showSnackBarMessage(message, duration)
    }

    private fun navigateTo(direction: Int) {
        findNavController().safeNavigation(direction)
    }
}