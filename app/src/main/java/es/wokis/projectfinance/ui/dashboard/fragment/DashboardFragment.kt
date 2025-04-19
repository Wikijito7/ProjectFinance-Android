package es.wokis.projectfinance.ui.dashboard.fragment

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.constants.AppConstants.SHOW_REACTION_TUTORIAL
import es.wokis.projectfinance.data.constants.AppConstants.SHOW_SWIPE_TUTORIAL
import es.wokis.projectfinance.databinding.DashboardInvoiceTutorialBinding
import es.wokis.projectfinance.databinding.FragmentDashboardBinding
import es.wokis.projectfinance.databinding.PopupReactionBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.dashboard.adapter.DashboardTabAdapter
import es.wokis.projectfinance.ui.dashboard.viewmodel.DashboardViewModel
import es.wokis.projectfinance.ui.dashboard.vo.InvoiceVO
import es.wokis.projectfinance.utils.applyEdgeToEdge
import es.wokis.projectfinance.utils.asCurrency
import es.wokis.projectfinance.utils.getCategoryName
import es.wokis.projectfinance.utils.getLocale
import es.wokis.projectfinance.utils.getTintByContrast
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.isShown
import es.wokis.projectfinance.utils.orZero
import es.wokis.projectfinance.utils.show
import es.wokis.projectfinance.utils.showReinsertInvoiceSnackBar
import es.wokis.projectfinance.utils.toDp
import java.util.Locale
import javax.inject.Inject
import androidx.core.graphics.toColorInt

@AndroidEntryPoint
class DashboardFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel by viewModels<DashboardViewModel>()

    private var binding: FragmentDashboardBinding? = null
    private var isAnimationCancelled = false
    private var reactionPopUp: PopupWindow? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getInvoices()
        setUpViewPager()
        setUpObservers()
        setUpTutorial()
    }

    override fun onResume() {
        super.onResume()
        setMenu(R.menu.summary_menu)
        setUpMenu()
        showAddInvoiceButton()
        setUpMenuListener()
        if (binding?.dashboardIncludeTutorial?.root.isShown()) {
            hideTutorialWindow()
        }
    }

    override fun showInvoiceRemovedSnackBar() {
        binding?.apply {
            showReinsertInvoiceSnackBar(root) {
                viewModel.reinsertRemovedInvoice()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpTutorial() {
        binding?.dashboardIncludeTutorial?.root?.applyEdgeToEdge(
            applyTopPadding = true,
            applyBottomPadding = true,
            applyLeftPadding = true,
            applyRightPadding = true
        )
    }

    private fun setUpMenu() {
        getMenu()?.apply {
            children.find {
                it.itemId == R.id.dashboard__menu__sync_now
            }?.isVisible = viewModel.isUserLoggedIn()
        }
    }

    private fun setUpListeners() {
        binding?.apply {
            dashboardIncludeTutorial.dashboardTutorialBtnClose.setOnClickListener {
                hideTutorialWindow()
            }

            dashboardIncludeTutorial.editInvoiceTypeBtnAccept.setOnClickListener {
                hideTutorialWindow()
            }

            dashboardIncludeTutorial.root.setOnClickListener {
                // no-op
            }
        }
    }

    private fun setUpObservers() {
        viewModel.getInvoicesLiveData().observe(viewLifecycleOwner) {
            it.takeIf { it.isNotEmpty() }?.let { invoices ->
                checkSwipeTutorial(invoices.first())
            }
        }
    }

    private fun setUpMenuListener() {
        onOptionsMenuClicked {
            when (it.itemId) {
                R.id.dashboard__menu__filter -> {
                    navigateToSelectFilter()
                    true
                }

                R.id.dashboard__menu__sync_now -> {
                    Toast.makeText(context, R.string.dashboard__synchronizing, Toast.LENGTH_SHORT)
                        .show()
                    launchSynchronizeWorker()
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateToSelectFilter() {
        closeAddInvoiceLayout()
        navigateTo(DashboardFragmentDirections.actionDashboardToSelectFilter())
    }

    private fun setUpViewPager() {
        setUpViewPagerAdapter()
        setUpTabLayout()
    }

    private fun setUpViewPagerAdapter() {
        binding?.apply {
            val tabsAdapter =
                DashboardTabAdapter(
                    viewModel.getDashboardTypes(),
                    { isScrolling ->
                        Log.d("TAG", "setUpViewPagerAdapter: scrolling $isScrolling")
                        setAddInvoiceButtonVisible(isScrolling.not())
                    },
                    this@DashboardFragment
                )
            dashboardPagerMainPager.adapter = tabsAdapter
            dashboardPagerMainPager.isUserInputEnabled = false
        }
    }

    private fun checkSwipeTutorial(invoice: InvoiceVO) {
        val showSwipeTutorial = sharedPreferences.getBoolean(SHOW_SWIPE_TUTORIAL, true)
        val showReactionsTutorial = sharedPreferences.getBoolean(SHOW_REACTION_TUTORIAL, true)
        if (showSwipeTutorial || showReactionsTutorial) {
            showTutorialWindow(invoice, showSwipeTutorial)
            sharedPreferences.edit {
                putBoolean(SHOW_SWIPE_TUTORIAL, false)
                putBoolean(SHOW_REACTION_TUTORIAL, false)
            }
        }
    }

    private fun showTutorialWindow(
        invoice: InvoiceVO,
        showSwipeTutorial: Boolean
    ) {
        hideToolbar()
        hideBottomNavigation()
        hideAddInvoiceButton()
        binding?.dashboardIncludeTutorial?.root.show()
        if (showSwipeTutorial) {
            executeDashboardAnimation()

        } else {
            executeReactionAnimation()
        }
        binding?.dashboardIncludeTutorial?.apply {
            editInvoiceTypeBtnAccept.apply {
                if (showSwipeTutorial) {
                    text = getString(R.string.dashboard_tutorial__next)
                    setOnClickListener {
                        dashboardTutorialContainerInvoice.clearAnimation()
                        isAnimationCancelled = true
                        setUpReactionTutorial()
                        executeReactionAnimation()
                    }

                } else {
                    setUpReactionTutorial()
                }
            }
        }
        setUpInvoiceView(invoice)
    }

    private fun DashboardInvoiceTutorialBinding.setUpReactionTutorial() {
        editInvoiceTypeBtnAccept.apply {
            text = getString(R.string.dashboard_tutorial__accept)
            setOnClickListener {
                closeTutorial()
            }
        }
        dashboardTutorialLabelDescription.text = getString(R.string.dashboard_tutorial__reaction_tutorial)
    }

    private fun DashboardInvoiceTutorialBinding.closeTutorial() {
        dashboardTutorialContainerInvoice.clearAnimation()
        reactionPopUp?.dismiss()
        reactionPopUp = null
        hideTutorialWindow()
    }

    private fun executeReactionAnimation() {
        binding?.dashboardIncludeTutorial?.apply {
            val animation = AnimationUtils.loadAnimation(root.context, R.anim.reaction_animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    // no-op
                }

                override fun onAnimationEnd(p0: Animation?) {
                    setUpPopUpWindow()
                    reactionPopUp?.showAsDropDown(
                        this@apply.dashboardTutorialContainerInvoice,
                        16.toDp(),
                        -this@apply.dashboardTutorialContainerInvoice.height.orZero(),
                        Gravity.CENTER_VERTICAL
                    )
                    dashboardTutorialImgPress.hide()
                }

                override fun onAnimationRepeat(p0: Animation?) {
                    // no-op
                }
            })
            dashboardTutorialImgPress.show()
            dashboardTutorialImgPress.apply {
                startAnimation(animation)
            }
        }
    }

    private fun setUpPopUpWindow() {
        reactionPopUp = PopupWindow().apply {
            val binding = PopupReactionBinding.inflate(layoutInflater)
            width = LinearLayout.LayoutParams.WRAP_CONTENT
            height = LinearLayout.LayoutParams.WRAP_CONTENT
            contentView = binding.root
            isFocusable = false
        }
    }

    private fun executeDashboardAnimation() {
        binding?.dashboardIncludeTutorial?.apply {
            val animation = AnimationUtils.loadAnimation(root.context, R.anim.dashboard_translation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    // no-op
                }

                override fun onAnimationEnd(p0: Animation?) {
                    if (isAnimationCancelled.not()) {
                        dashboardTutorialContainerInvoice.apply {
                            startAnimation(animation)
                        }
                    }
                }

                override fun onAnimationRepeat(p0: Animation?) {
                    // no-op
                }
            })

            dashboardTutorialContainerInvoice.apply {
                startAnimation(animation)
            }
        }
    }

    private fun setUpInvoiceView(invoice: InvoiceVO) {
        setUpInvoiceInfo(invoice)
        setUpInvoiceIcon(invoice)
    }

    private fun setUpInvoiceInfo(invoice: InvoiceVO) {
        binding?.dashboardIncludeTutorial?.dashboardTutorialIncludeInvoice?.apply {
            val context = root.context
            val locale: Locale = context.getLocale()
            rowDashboardInvoiceLabelTitle.text = invoice.title
            rowDashboardInvoiceLabelDate.text = invoice.date
            rowDashboardInvoiceLabelCategory.text = context.getCategoryName(invoice.categoryName)
            rowDashboardInvoiceImgCategory.drawable.setTint(invoice.categoryColor.toColorInt())
            rowDashboardInvoiceLabelInvoiceQuantity.text = invoice.quantity.asCurrency(locale)
        }
    }

    private fun setUpInvoiceIcon(invoice: InvoiceVO) {
        binding?.dashboardIncludeTutorial?.dashboardTutorialIncludeInvoice?.rowDashboardInvoiceImgInvoiceTypeIcon?.apply {
            val categoryColor = invoice.categoryColor.toColorInt()
            setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    when (invoice.type) {
                        InvoiceType.EXPENSE -> {
                            R.drawable.ic_expense
                        }

                        else -> R.drawable.ic_deposit
                    },
                    context.theme,
                )
            )
            background?.setTint(categoryColor)
            drawable?.setTint(getTintByContrast(categoryColor))
        }
    }

    private fun hideTutorialWindow() {
        binding?.apply {
            isAnimationCancelled = true
            dashboardIncludeTutorial.apply {
                root.hide()
                dashboardTutorialContainerInvoice.clearAnimation()
            }
        }
        showAddInvoiceButton()
        showToolbar()
        showBottomNavigation()
    }


    private fun setUpTabLayout() {
        binding?.apply {
            TabLayoutMediator(dashboardTabsInvoices, dashboardPagerMainPager) { tab, position ->
                tab.text = getString(
                    when (position) {
                        0 -> R.string.dashboard__all_invoices_tab
                        1 -> R.string.dashboard__deposits_tab
                        else -> R.string.dashboard__expenses_tab
                    }
                )
            }.attach()
        }
    }
}