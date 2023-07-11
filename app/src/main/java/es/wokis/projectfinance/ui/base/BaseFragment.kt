package es.wokis.projectfinance.ui.base

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.constants.AppConstants
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.ui.MainActivity
import es.wokis.projectfinance.utils.isTrue
import es.wokis.projectfinance.utils.safeNavigation

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    open fun getScreenName(): String {
        return getString(R.string.app_name)
    }

    open fun showInvoiceRemovedSnackBar() {
        // no-op
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFragmentResultListener()
    }

    override fun onResume() {
        super.onResume()
        setUpToolbarTitle()
        clearMenu()
        hideAddInvoiceButton()
        showToolbar()
        showBottomNavigation()
        setLoading(false)
    }

    fun navigateTo(direction: NavDirections) {
        findNavController().safeNavigation(direction)
    }

    open fun navigateBack() {
        findNavController().navigateUp()
    }

    fun interceptBackPressed(callback: () -> Unit) {
        activity?.let {
            findNavController().setOnBackPressedDispatcher(it.onBackPressedDispatcher.apply {
                addCallback(viewLifecycleOwner) {
                    callback()
                }
            })
        }
    }

    fun setMenu(@MenuRes menu: Int) {
        (activity as? MainActivity)?.setMenu(menu)
    }

    fun clearMenu() {
        (activity as? MainActivity)?.clearMenu()
    }

    fun getMenu() = (activity as? MainActivity)?.getMenu()

    fun onOptionsMenuClicked(handler: (optionSelected: MenuItem) -> Boolean) {
        (activity as? MainActivity)?.onOptionsMenuClicked(handler)
    }

    fun showToolbar() {
        (activity as? MainActivity)?.showToolbar()
    }

    fun hideToolbar() {
        (activity as? MainActivity)?.hideToolbar()
    }

    fun hideAddInvoiceButton() {
        (activity as? MainActivity)?.hideAddInvoiceButton()
    }

    fun showAddInvoiceButton() {
        (activity as? MainActivity)?.showAddInvoiceButton()
    }

    fun setAddInvoiceButtonVisible(visible: Boolean) {
        (activity as? MainActivity)?.setAddInvoiceButtonVisible(visible)
    }

    fun showBottomNavigation() {
        (activity as? MainActivity)?.showBottomNavigation()
    }

    fun hideBottomNavigation() {
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    fun closeAddInvoiceLayout() {
        (activity as? MainActivity)?.closeAddInvoiceLayout()
    }

    fun setLoading(loading: Boolean, loadingText: String = AppConstants.EMPTY_TEXT) {
        (activity as? MainActivity)?.setLoading(loading, loadingText)
    }

    fun shouldAskForNotificationPermissions() =
        (activity as? MainActivity)?.shouldAskForNotificationPermissions().isTrue()

    fun setUpSecureMode() {
        (activity as? MainActivity)?.setUpSecureMode()
    }

    fun setUpTheme() {
        (activity as? MainActivity)?.setUpTheme()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isPermissionDenied(): Boolean = (activity as? MainActivity)?.isPermissionDenied().isTrue()

    protected fun launchSynchronizeWorker() {
        (activity as? MainActivity)?.launchSynchronizeWorker()
    }

    private fun setUpFragmentResultListener() {
        invoiceRemovedFragmentResultListener()
    }

    private fun invoiceRemovedFragmentResultListener() {
        setFragmentResultListener(RequestKeys.DETAIL_REMOVE_INVOICE) { _, _ ->
            onInvoiceRemoved()
        }
    }

    private fun onInvoiceRemoved() {
        showInvoiceRemovedSnackBar()
    }

    private fun navigateTo(direction: Int) {
        findNavController().safeNavigation(direction)
    }

    private fun setUpToolbarTitle() {
        (activity as? MainActivity)?.setToolbarTitle(getScreenName().takeIf { it.isNotBlank() }
            ?: getString(R.string.app_name)
        )
    }
}