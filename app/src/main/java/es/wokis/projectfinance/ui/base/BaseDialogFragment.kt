package es.wokis.projectfinance.ui.base

import androidx.fragment.app.DialogFragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import es.wokis.projectfinance.utils.safeNavigation

abstract class BaseDialogFragment : DialogFragment() {

    fun navigateTo(direction: NavDirections) {
        findNavController().safeNavigation(direction)
    }

    private fun navigateTo(direction: Int) {
        findNavController().safeNavigation(direction)
    }

    fun navigateBack() {
        findNavController().navigateUp()
    }
}