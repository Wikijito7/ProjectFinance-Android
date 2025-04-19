package es.wokis.projectfinance.ui

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.MobileNavigationDirections
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.data.enums.SelectedThemeEnum
import es.wokis.projectfinance.databinding.ActivityMainBinding
import es.wokis.projectfinance.ui.home.fragment.HomeFragmentDirections
import es.wokis.projectfinance.utils.applyEdgeToEdge
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.hideAnim
import es.wokis.projectfinance.utils.safeNavigation
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.show
import es.wokis.projectfinance.utils.showAnim
import es.wokis.projectfinance.utils.showSnackBar
import es.wokis.projectfinance.worker.SynchronizeWorker

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val controller by lazy {
        findNavController(R.id.main__container__nav_host_fragment)
    }

    private var binding: ActivityMainBinding? = null

    private val viewModel: MainActivityViewModel by viewModels()

    private var showingButtons = false

    private var currentIconSelected: Int = R.id.homeFragment

    private val onItemSelectedListener = NavigationBarView.OnItemSelectedListener {
        if (currentIconSelected != it.itemId) {
            currentIconSelected = it.itemId
            controller.safeNavigation(
                when (it.itemId) {
                    R.id.homeFragment -> MobileNavigationDirections.actionGlobalToHome()
                    R.id.profileFragment -> MobileNavigationDirections.actionGlobalToProfile()
                    R.id.dashboardFragment -> MobileNavigationDirections.actionGlobalToDashboard()
                    else -> MobileNavigationDirections.actionGlobalToHome()
                }
            )
            closeAddInvoiceLayout()
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setUpSecureMode()
        setUpTheme()
        setContentView(binding?.root)
        setUpBottomNav()
        setUpAds()
        checkNotificationPermissions()
        binding?.apply {
            mainToolbarMainToolbar.applyEdgeToEdge(
                applyTopPadding = true,
                applyLeftPadding = true,
                applyRightPadding = true
            )
            setUpFloatingButtonListeners()
        }
    }

    override fun onResume() {
        super.onResume()
        launchSynchronizeWorker()
    }

    override fun onPause() {
        launchSynchronizeWorker()
        super.onPause()
    }

    override fun onDestroy() {
        clearMenu()
        binding = null
        super.onDestroy()
    }

    fun setMenu(@MenuRes menu: Int) {
        binding?.apply {
            mainToolbarMainToolbar.inflateMenu(menu)
        }
    }

    fun clearMenu() {
        binding?.mainToolbarMainToolbar?.menu?.clear()
    }

    fun onOptionsMenuClicked(handler: (optionSelected: MenuItem) -> Boolean) {
        binding?.mainToolbarMainToolbar?.setOnMenuItemClickListener {
            handler(it)
        }
    }

    fun setToolbarTitle(title: String) {
        binding?.mainToolbarMainToolbar?.title = title
    }

    fun setUpSecureMode() {
        if (viewModel.isSecureModeEnabled()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )

        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun setUpTheme() {
        AppCompatDelegate.setDefaultNightMode(
            when (viewModel.getSelectedTheme()) {
                SelectedThemeEnum.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                SelectedThemeEnum.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                SelectedThemeEnum.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun showToolbar() {
        binding?.mainToolbarMainToolbar.show()
    }

    fun showBottomNavigation() {
        binding?.mainNavBottomNav.show()
    }

    fun hideToolbar() {
        binding?.mainToolbarMainToolbar.hide()
    }

    fun hideBottomNavigation() {
        binding?.mainNavBottomNav.hide()
    }

    fun showAddInvoiceButton() {
        binding?.mainIncludeAddInvoice?.root.show()
    }

    fun setAddInvoiceButtonVisible(visible: Boolean) {
        if (visible) {
            binding?.mainIncludeAddInvoice?.root.showAnim()

        } else {
            binding?.mainIncludeAddInvoice?.root.hideAnim()
        }
    }

    fun closeAddInvoiceLayout() {
        resetFloatingButtonStatus()
    }

    fun hideAddInvoiceButton() {
        binding?.mainIncludeAddInvoice?.root.hide()
    }

    fun setLoading(loading: Boolean, loadingText: String = EMPTY_TEXT) {
        binding?.mainContainerLoading?.loadingContainerMain.setVisible(loading)
        binding?.mainContainerLoading?.loadingLabelLoadingText?.text = loadingText
    }

    fun launchSynchronizeWorker() {
        if (viewModel.isUserLoggedIn().not()) return
        val request = OneTimeWorkRequest.from(SynchronizeWorker::class.java)
        WorkManager.getInstance(this)
            .enqueueUniqueWork(
                SYNC_WORKER,
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

    fun shouldAskForNotificationPermissions() = viewModel.isUserLoggedIn() &&
            NotificationManagerCompat.from(this).areNotificationsEnabled().not() &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            isPermissionDenied().not()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isPermissionDenied(): Boolean {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        return ContextCompat.checkSelfPermission(
            this, permission
        ) != PERMISSION_DENIED
    }

    fun getMenu() = binding?.mainToolbarMainToolbar?.menu

    fun showSnackBarMessage(@StringRes message: Int, duration: Int = 5000) {
        binding?.mainToolbarMainToolbar?.let {
            showSnackBar(
                title = message,
                view = it,
                snackBarDuration = duration
            )
        }
    }

    private fun setUpAds() {
        MobileAds.initialize(
            this
        ) {
            Log.d("MainActivity", "setUpAds: $it")
        }
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("ED2997B3D5D804C47CE42C7E286959BE"))
                .build()
        )
    }

    private fun setUpBottomNav() {
        binding?.mainNavBottomNav?.setupWithNavController(controller)
        setUpOnDestinationChangedListener()
        setOnItemSelectedListener()
    }

    private fun setUpOnDestinationChangedListener() {
        controller.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.dashboardFragment, R.id.profileFragment ->
                    currentIconSelected = destination.id
            }
        }
    }

    private fun ActivityMainBinding.setUpFloatingButtonListeners() {
        mainIncludeAddInvoice.floatingButtonBtnDeposit.setOnClickListener {
            navigateToAddInvoice(InvoiceType.DEPOSIT.key)
        }

        mainIncludeAddInvoice.floatingButtonBtnExpense.setOnClickListener {
            navigateToAddInvoice(InvoiceType.EXPENSE.key)
        }

        mainIncludeAddInvoice.floatingButtonLabelDeposit.setOnClickListener {
            navigateToAddInvoice(InvoiceType.DEPOSIT.key)
        }

        mainIncludeAddInvoice.floatingButtonLabelExpense.setOnClickListener {
            navigateToAddInvoice(InvoiceType.EXPENSE.key)
        }

        mainIncludeAddInvoice.floatingButtonViewBackground.setOnClickListener {
            resetFloatingButtonStatus()
        }
        setUpTransitionListener()
    }

    private fun setOnItemSelectedListener() {
        binding?.mainNavBottomNav?.setOnItemSelectedListener(onItemSelectedListener)
    }

    private fun ActivityMainBinding.setUpTransitionListener() {
        mainIncludeAddInvoice.root.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                updateFloatingButton()
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                // no-op
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                // no-op
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                // no-op
            }

        })
    }

    private fun navigateToAddInvoice(invoiceType: String) {
        controller.navigate(
            HomeFragmentDirections.actionGlobalToAddInvoice(
                NO_INVOICE_ID,
                invoiceType
            )
        )
        resetFloatingButtonStatus()
    }

    private fun resetFloatingButtonStatus() {
        if (showingButtons) {
            binding?.mainIncludeAddInvoice?.floatingButtonBtnAddInvoice?.performClick()
        }
    }

    private fun updateFloatingButton() {
        binding?.apply {
            showingButtons = showingButtons.not()
            mainIncludeAddInvoice.floatingButtonBtnAddInvoice.animate().apply {
                rotationBy(
                    if (showingButtons) {
                        ROTATION

                    } else {
                        -ROTATION
                    }
                )
                duration = ROTATION_ANIM_DURATION
                start()
            }
        }
    }

    private fun checkNotificationPermissions() {
        if (shouldAskForNotificationPermissions()) {
            controller.safeNavigation(MobileNavigationDirections.actionGlobalToNotificationRationale())
        }
    }

    companion object {
        private const val NO_INVOICE_ID = 0L
        private const val ROTATION = 45f
        private const val ROTATION_ANIM_DURATION: Long = 50
        private const val SYNC_WORKER = "SYNC_WORKER"
    }
}