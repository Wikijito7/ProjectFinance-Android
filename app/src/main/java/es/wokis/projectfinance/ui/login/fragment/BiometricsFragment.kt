package es.wokis.projectfinance.ui.login.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import es.wokis.projectfinance.R
import es.wokis.projectfinance.databinding.FragmentBiometricsBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class BiometricsFragment : BaseFragment() {
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null
    private var binding: FragmentBiometricsBinding? = null
    private var executor: Executor? = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBiometricsBinding.inflate(inflater, container, false)
        initializeListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBiometrics()
        showBiometricsDialog()
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
        hideBottomNavigation()
        hideAddInvoiceButton()
    }

    override fun onDestroyView() {
        binding = null
        biometricPrompt = null
        promptInfo = null
        executor = null
        super.onDestroyView()
    }

    private fun setUpBiometrics() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometrics__login))
            .setSubtitle(getString(R.string.biometrics__subtitle))
            .setNegativeButtonText(getString(R.string.general__back_desc))
            .setConfirmationRequired(true)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        executor?.let { executorNotNull ->
            biometricPrompt = BiometricPrompt(this@BiometricsFragment,
                executorNotNull,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        activity?.runOnUiThread {
                            navigateTo(BiometricsFragmentDirections.actionBiometricsToHome())
                        }
                    }
                }
            )
        }
    }

    private fun initializeListeners() {
        binding?.apply {
            biometricsContainerFingerprint.setOnClickListener {
                showBiometricsDialog()
            }
        }
    }

    private fun showBiometricsDialog() {
        promptInfo?.let { biometrics ->
            biometricPrompt?.authenticate(biometrics)
        }
    }
}