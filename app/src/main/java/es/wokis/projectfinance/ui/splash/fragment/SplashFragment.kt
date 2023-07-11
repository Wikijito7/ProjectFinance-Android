package es.wokis.projectfinance.ui.splash.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.BuildConfig
import es.wokis.projectfinance.R
import es.wokis.projectfinance.databinding.FragmentSplashBinding
import es.wokis.projectfinance.ui.MainActivity
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.splash.viewmodel.SplashViewModel

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    private var binding: FragmentSplashBinding? = null

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        setUpView()
        return binding?.root
    }

    private fun setUpView() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        binding?.splashImgAppIcon?.startAnimation(animation)
        binding?.splashLabelVersion?.text = getString(R.string.splash__version, BuildConfig.VERSION_NAME)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkCategories()
        viewModel.checkInvoicesWithoutCategory()
        viewModel.checkReactionsWithoutInvoice()
        checkAndroidSplash()
        setUpObservers()
        setUpNavigationDelay()
    }

    private fun checkAndroidSplash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            navigateTo(SplashFragmentDirections.actionGlobalToHome())
        }
    }

    override fun showInvoiceRemovedSnackBar() {
        // no-op
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.apply {
            hideToolbar()
            hideBottomNavigation()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpObservers() {
        viewModel.getNavigationPendingLiveData().observe(viewLifecycleOwner) {
            // if (viewModel.isBiometricsEnabled()) {
            //    navigateTo(SplashFragmentDirections.actionSplashToBiometrics())

            // } else {
                navigateTo(SplashFragmentDirections.actionGlobalToHome())
            // }
        }
    }

    private fun setUpNavigationDelay() {
        binding?.root?.postDelayed({
            activity?.runOnUiThread {
                viewModel.setNavigationPending()
            }
        }, SPLASH_DELAY)
    }

    companion object {
        const val SPLASH_DELAY = 1000L
    }
}