package es.wokis.projectfinance.ui.profile.fragment

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_NO_BIOMETRICS
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import es.wokis.projectfinance.MobileNavigationDirections
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.data.enums.SelectedThemeEnum
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.data.response.ErrorType
import es.wokis.projectfinance.databinding.FragmentConfigBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.profile.viewmodel.ConfigViewModel
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.showSnackBar

class ConfigFragment : BaseFragment() {
    private var binding: FragmentConfigBinding? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfigBinding.inflate(inflater, container, false)
        initializeListeners()
        setUpView()
        return binding?.root
    }

    override fun getScreenName(): String = getString(R.string.title_config)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFragmentResultListener()
        setUpObservers()
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpObservers() {
        setUpCloseAllSessionsObserver()
        setUpRemoveTOTPObserver()
    }

    private fun setUpRemoveTOTPObserver() {
        viewModel.getRemoveTotpLiveData().observe(viewLifecycleOwner) { event ->
            event.getDataIfNotHandled()?.let {
                when (it) {
                    is AsyncResult.Error -> {
                        when (it.error) {
                            is ErrorType.TOTPRequiredError -> {
                                navigateTo(
                                    ConfigFragmentDirections.actionConfigToTotpDialog(
                                        REMOVE_TOTP_KEY
                                    )
                                )
                            }

                            else -> {
                                setLoading(false)
                                binding?.apply {
                                    showSnackBar(
                                        R.string.config__error_removing_totp,
                                        view = configContainerAccount
                                    )
                                }
                            }
                        }
                    }

                    is AsyncResult.Loading -> {
                        setLoading(true, getString(R.string.config__removing_totp))
                    }

                    is AsyncResult.Success -> {
                        setLoading(false)
                        viewModel.closeAllSessions()
                    }
                }
            }
        }
    }

    private fun setUpCloseAllSessionsObserver() {
        viewModel.getCloseAllSessionsLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    setLoading(false)
                    navigateToHome()
                }

                is AsyncResult.Loading -> {
                    setLoading(true, getString(R.string.config__closing_sessions))
                }

                is AsyncResult.Success -> {
                    setLoading(false)
                    navigateToHome()
                }
            }
        }
    }

    private fun navigateToHome() {
        navigateTo(MobileNavigationDirections.actionGlobalToHome())
        binding?.configContainerAccount?.apply {
            showSnackBar(
                R.string.login__sign_out_successfully,
                view = this,
                snackBarDuration = SNACK_BAR_DURATION
            )
        }
    }

    private fun setUpView() {
        binding?.apply {
            configSwitchOtp.isChecked = viewModel.isTwoFactorAuthEnabled()
            configSwitchSecure.isChecked = viewModel.isSecureModeEnabled()
            setUpBiometrics()
            setUpThemeSelector()
            setUpNotifications()
        }
    }

    private fun setUpFragmentResultListener() {
        setUpThemeSelectedListener()
        setUpTotpListener()
        setUpRemoveTotpListener()
    }

    private fun setUpRemoveTotpListener() {
        setFragmentResultListener(
            REMOVE_TOTP_KEY,
        ) { _, _ ->
            viewModel.removeTOTP()
        }
    }

    private fun setUpTotpListener() {
        setFragmentResultListener(
            RequestKeys.CONFIG_TO_TOTP
        ) { _, _ ->
            setUpView()
        }
    }

    private fun setUpThemeSelectedListener() {
        setFragmentResultListener(
            RequestKeys.CONFIG_TO_THEME_SELECTOR
        ) { _, bundle ->
            val selectedTheme = SelectedThemeEnum.fromKey(bundle.getString(SELECTED_THEME))
            onThemeSelected(selectedTheme)
        }
    }

    private fun FragmentConfigBinding.setUpBiometrics() {
        context?.let {
            val noHardware = BiometricManager.from(it)
                .canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
            if (noHardware) {
                configContainerSecure.hide()
                return
            }
            configSwitchBiometrics.isChecked = viewModel.isBiometricsEnabled()
            setUpBiometricsPrompt()
        }
    }

    private fun FragmentConfigBinding.setUpNotifications() {
        context?.let {
            val isLowerThanTiramisu = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            configLabelPermissionTitle.setVisible(isLowerThanTiramisu.not())
            configContainerNotifications.setVisible(isLowerThanTiramisu.not()) {
                configSwitchNotifications.isChecked =
                    NotificationManagerCompat.from(it).areNotificationsEnabled()
            }
        }
    }

    private fun FragmentConfigBinding.setUpThemeSelector() {
        when (viewModel.getSelectedTheme()) {
            SelectedThemeEnum.LIGHT -> {
                configLabelThemeDesc.text = getString(R.string.config__light_mode)
                configImgTheme.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_light_mode,
                        context?.theme
                    )
                )
            }

            SelectedThemeEnum.DARK -> {
                configLabelThemeDesc.text = getString(R.string.config__dark_mode)
                configImgTheme.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_dark_mode,
                        context?.theme
                    )
                )
            }

            SelectedThemeEnum.SYSTEM -> {
                configLabelThemeDesc.text = getString(R.string.config__system_default)
                configImgTheme.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_phone_default,
                        context?.theme
                    )
                )
            }
        }
    }

    private fun onThemeSelected(theme: SelectedThemeEnum) {
        binding?.apply {
            viewModel.setSelectedTheme(theme)
            setUpThemeSelector()
            setUpTheme()
        }
    }

    private fun initializeListeners() {
        binding?.apply {
            configContainerBiometrics.setOnClickListener {
                showBiometricsDialog()
            }

            configContainerSecure.setOnClickListener {
                onClickSecureMode()
            }

            configContainerTheme.setOnClickListener {
                navigateTo(ConfigFragmentDirections.actionConfigToThemeSelector())
            }

            configContainerAccount.setOnClickListener {
                showCloseSessionsDialog()
            }

            configBtnGoBack.setOnClickListener {
                navigateBack()
            }

            configContainerOtp.setOnClickListener {
                onTotpClicked()
            }
        }
    }

    private fun FragmentConfigBinding.onTotpClicked() {
        if (configSwitchOtp.isChecked.not()) {
            navigateTo(ConfigFragmentDirections.actionConfigToTotp())

        } else {
            showRemoveTotpDialog()
        }
    }

    private fun showRemoveTotpDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.config__remove_totp)
                .setMessage(R.string.config__remove_totp_desc)
                .setNegativeButton(R.string.general__cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(R.string.general__accept) { dialog, _ ->
                    viewModel.removeTOTP()
                    dialog.cancel()
                }
                .create()
                .show()
        }
    }

    private fun showCloseSessionsDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.config__close_all_sessions)
                .setMessage(R.string.config__close_all_sessions_dialog_desc)
                .setNegativeButton(R.string.general__cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(R.string.general__accept) { dialog, _ ->
                    viewModel.closeAllSessions()
                    dialog.cancel()
                }
                .create()
                .show()
        }
    }

    private fun onClickSecureMode() {
        binding?.apply {
            val isChecked = configSwitchSecure.isChecked
            viewModel.shouldActivateSecureMode(isChecked.not())
            configSwitchSecure.isChecked = isChecked.not()
            setUpSecureMode()
        }
    }

    private fun setUpBiometricsPrompt() {
        context?.let {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.config__biometrics))
                .setSubtitle(getString(R.string.config__biometrics_confirm))
                .setNegativeButtonText(getString(R.string.general__back_desc))
                .setConfirmationRequired(true)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()

            biometricPrompt = BiometricPrompt(this@ConfigFragment,
                ContextCompat.getMainExecutor(it),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        if (errorCode == ERROR_NO_BIOMETRICS) {
                            Toast.makeText(
                                it,
                                "No credentials set up",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Toast.makeText(
                            it,
                            "Authentication error: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                        biometricPrompt?.cancelAuthentication()
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        binding?.apply {
                            val checked = configSwitchBiometrics.isChecked
                            configSwitchBiometrics.isChecked = checked.not()
                            viewModel.shouldActivateBiometrics(checked.not())
                            Toast.makeText(
                                it,
                                getString(
                                    if (checked) {
                                        R.string.config__biometrics_disabled

                                    } else {
                                        R.string.config__biometrics_enabled
                                    }
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(
                            it,
                            "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }

    private fun showBiometricsDialog() {
        promptInfo?.let { biometrics ->
            biometricPrompt?.authenticate(biometrics)
        }
    }

    companion object {
        const val SELECTED_THEME = "SELECTED_THEME"
        private const val REMOVE_TOTP_KEY = "REMOVE_TOTP_KEY"
        private const val SNACK_BAR_DURATION = 1500
    }

}