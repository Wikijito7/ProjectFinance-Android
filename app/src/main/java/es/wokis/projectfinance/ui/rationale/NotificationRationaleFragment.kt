package es.wokis.projectfinance.ui.rationale

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import es.wokis.projectfinance.MobileNavigationDirections
import es.wokis.projectfinance.R
import es.wokis.projectfinance.databinding.FragmentNotificationRationaleBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.home.fragment.HomeFragmentDirections
import es.wokis.projectfinance.utils.showSnackBar

class NotificationRationaleFragment : BaseFragment() {

    private var binding: FragmentNotificationRationaleBinding? = null

    private val openSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        navigateTo(MobileNavigationDirections.actionGlobalToHome())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                binding?.apply {
                    showSnackBar(
                        R.string.notifications__notifications_disabled,
                        R.string.notifications__activate_notifications,
                        root,
                        action = {
                            navigateTo(HomeFragmentDirections.actionGlobalToNotificationRationale())
                        })
                }
            }
            navigateTo(MobileNavigationDirections.actionGlobalToHome())
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationRationaleBinding.inflate(inflater, container, false)
        initializeListeners()
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
        hideBottomNavigation()
        hideAddInvoiceButton()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun initializeListeners() {
        binding?.apply {
            notificationBtnSkip.setOnClickListener {
                navigateTo(MobileNavigationDirections.actionGlobalToHome())
            }

            notificationBtnNotify.setOnClickListener {
                checkNotificationPermissions()
            }
        }
    }

    private fun checkNotificationPermissions() {
        context?.let {
            if (shouldAskForNotificationPermissions().not()) {
                navigateTo(MobileNavigationDirections.actionGlobalToHome())
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                isPermissionDenied()
            ) {
                openSettingsLauncher.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", it.packageName, null)
                })
            }

            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )

            } else {
                navigateTo(MobileNavigationDirections.actionGlobalToHome())
            }
        }
    }
}