package es.wokis.projectfinance.ui.home.fragment

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.constants.AppConstants
import es.wokis.projectfinance.databinding.FragmentHomeBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.home.adapter.HomeInvoiceAdapter
import es.wokis.projectfinance.ui.home.viewmodel.HomeViewModel
import es.wokis.projectfinance.utils.asCurrency
import es.wokis.projectfinance.utils.getLocale
import es.wokis.projectfinance.utils.getPrimaryColor
import es.wokis.projectfinance.utils.getTotalDepositAsDouble
import es.wokis.projectfinance.utils.getTotalExpenseAsDouble
import es.wokis.projectfinance.utils.getTotalSavedAsDouble
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.isTrue
import es.wokis.projectfinance.utils.orZero
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.show
import es.wokis.projectfinance.utils.showReinsertInvoiceSnackBar
import es.wokis.projectfinance.utils.toMonetaryDouble
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: HomeViewModel by viewModels()

    private var binding: FragmentHomeBinding? = null
    private var adapter: HomeInvoiceAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setUpAdapter()
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        viewModel.getInvoices()
        viewModel.getAllInvoices()
        viewModel.getLastInvoices()
    }

    override fun onResume() {
        super.onResume()
        interceptBackPressed {
            activity?.finishAffinity()
        }
        showAddInvoiceButton()
        showChartsAnimation()
    }

    private fun showChartsAnimation() {
        val showTutorial = sharedPreferences.getBoolean(AppConstants.SHOW_CHARTS_TUTORIAL, true)
        if (showTutorial &&
            areChartsVisible()
        ) {
            smoothScrollCharts()
            sharedPreferences.edit {
                putBoolean(AppConstants.SHOW_CHARTS_TUTORIAL, false)
            }
        }
    }

    private fun areChartsVisible() =
        binding?.homeContainerAllCharts?.children?.takeIf { it.count() > 0 }?.all { it.isVisible }.isTrue()

    private fun smoothScrollCharts() {
        binding?.apply {
            homeContainerScrollCharts.apply {
                postDelayed(ANIMATION_DELAY) {
                    root.apply {
                        smoothScrollTo(0, height, SMOOTH_VERTICAL_SCROLL)
                    }
                    showPopUp()
                    hideAddInvoiceButton()
                    val animator =
                        ObjectAnimator.ofInt(this, "scrollX", width)
                    animator.duration = SMOOTH_HORIZONTAL_SCROLL
                    animator.start()
                    animator.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator) {
                            // no-op
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            val endAnimator =
                                ObjectAnimator.ofInt(homeContainerScrollCharts, "scrollX", -width)
                            endAnimator.duration = SMOOTH_HORIZONTAL_SCROLL
                            endAnimator.start()
                            hidePopUp()
                            showAddInvoiceButton()
                        }

                        override fun onAnimationCancel(p0: Animator) {
                            // no-op
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                            // no-op
                        }

                    })
                }
            }
        }
    }

    private fun FragmentHomeBinding.showPopUp() {
        homePopupMoreContent.root.apply {
            val animation = AnimationUtils.loadAnimation(root.context, R.anim.fade_in)
            show()
            homePopupMoreContent.root.startAnimation(animation)
        }
    }

    private fun FragmentHomeBinding.hidePopUp() {
        homePopupMoreContent.root.apply {
            val animation = AnimationUtils.loadAnimation(root.context, R.anim.fade_out)
            homePopupMoreContent.root.startAnimation(animation)
            homePopupMoreContent.root.postDelayed(animation.duration) {
                homePopupMoreContent.root.hide()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun showInvoiceRemovedSnackBar() {
        binding?.apply {
            showReinsertInvoiceSnackBar(root) {
                viewModel.reinsertRemovedInvoice()
            }
        }
    }

    private fun setUpAdapter() {
        adapter = HomeInvoiceAdapter()
        adapter?.setListener {
            navigateTo(HomeFragmentDirections.actionHomeToDetail(it))
        }
        binding?.homeListInvoices?.adapter = adapter
    }

    private fun setUpListeners() {
        binding?.apply {
            homeLabelShowMore.setOnClickListener {
                navigateTo(HomeFragmentDirections.actionHomeToDashboard())
            }

            homeContainerAllCharts.setOnClickListener {
                navigateTo(HomeFragmentDirections.actionHomeToGraphs())
            }
        }
    }

    private fun setUpObservers() {
        observeInvoiceLiveData()
        observeAllInvoicesLiveData()
        observeHomeInvoiceLiveData()
    }

    private fun observeAllInvoicesLiveData() {
        viewModel.getAllInvoicesLiveData().observe(viewLifecycleOwner) {
            setUpHistoricalChart(it)
            showChartsAnimation()
        }
    }

    private fun observeHomeInvoiceLiveData() {
        viewModel.getHomeInvoicesLiveData().observe(viewLifecycleOwner) {
            binding?.apply {
                homeListInvoices.setVisible(it.isNotEmpty()) {
                    adapter?.setInvoices(it)
                }
                homeLabelNoMovements.setVisible(it.isEmpty())
            }
        }
    }

    private fun observeInvoiceLiveData() {
        viewModel.getInvoicesLiveData().observe(viewLifecycleOwner) {
            binding?.apply {
                setUpHeader(it)
                setUpCharts(it)
            }
        }
    }

    private fun setUpHistoricalChart(invoices: List<InvoiceBO>) {
        context?.let {
            binding?.homeIncludeSavingsHistorical?.apply {
                val drawColor = it.theme.getPrimaryColor() ?: ResourcesCompat.getColor(
                    it.resources,
                    R.color.orange,
                    it.theme
                )
                val chartData = viewModel.generateSavingsHistoricalData(invoices, drawColor)
                if (chartData?.entryCount.orZero() < 2) {
                    root.hide()
                    return
                }
                root.show()
                homeLabelSavingsTitle.text = getString(R.string.home__historical_savings)
                homeChartSavings.apply {
                    setUpLineChart()
                    data = chartData
                    animateXY(
                        CURRENCY_ANIMATION.toInt(),
                        CURRENCY_ANIMATION.toInt()
                    )
                }
            }
        }
    }

    private fun setUpCharts(invoices: List<InvoiceBO>) {
        context?.let {
            binding?.apply {
                setUpExpensesChart(invoices)
                setUpDepositsChart(invoices)
                showChartsAnimation()
            }
        }
    }

    private fun FragmentHomeBinding.setUpDepositsChart(invoices: List<InvoiceBO>) {
        homeIncludeCategoriesDeposits.apply {
            val chartData = viewModel.generateDepositPerCategoryPieData(invoices)
            if (chartData?.dataSet?.entryCount.orZero() == 0) {
                root.hide()
                return
            }
            root.show()
            homeLabelPieChartTitle.text =
                getString(R.string.home__deposit_category_pie_chart)
            homeChartPieChart.apply {
                setUpPieChart()
                data = viewModel.generateDepositPerCategoryPieData(invoices)
                animateXY(
                    CURRENCY_ANIMATION.toInt(),
                    CURRENCY_ANIMATION.toInt()
                )
            }
        }
    }

    private fun FragmentHomeBinding.setUpExpensesChart(invoices: List<InvoiceBO>) {
        homeIncludeCategoriesExpenses.apply {
            val chartData = viewModel.generateExpensesPerCategoryPieData(invoices)
            if (chartData?.dataSet?.entryCount.orZero() == 0) {
                root.hide()
                return
            }
            root.show()
            homeLabelPieChartTitle.text =
                getString(R.string.home__expense_category_pie_chart)
            homeChartPieChart.apply {
                setUpPieChart()
                data = chartData
                animateXY(
                    CURRENCY_ANIMATION.toInt(),
                    CURRENCY_ANIMATION.toInt()
                )
            }
        }
    }

    private fun LineChart.setUpLineChart() {
        axisLeft.apply {
            isEnabled = false
        }
        axisRight.apply {
            isEnabled = false
        }
        xAxis.isEnabled = false
        description.isEnabled = false
        legend.isEnabled = false
        setTouchEnabled(false)
    }

    private fun PieChart.setUpPieChart() {
        description.isEnabled = false
        legend.isEnabled = false
        setTouchEnabled(false)
        setHoleColor(Color.TRANSPARENT)
    }

    private fun FragmentHomeBinding.setUpHeader(invoices: List<InvoiceBO>) {
        context?.let { contextNotNull ->
            val locale = contextNotNull.getLocale()

            getCurrencyAnimated(invoices.getTotalSavedAsDouble().toMonetaryDouble()) {
                it?.let { currentValue ->
                    homeIncludeHeader.savingsLabelTotalSaved.text = currentValue.asCurrency(locale)
                }
            }

            getCurrencyAnimated(invoices.getTotalDepositAsDouble().toMonetaryDouble()) {
                it?.let { currentValue ->
                    homeIncludeHeader.savingsLabelTotalDepositQuantity.text =
                        currentValue.asCurrency(locale)
                }
            }

            getCurrencyAnimated(invoices.getTotalExpenseAsDouble().toMonetaryDouble()) {
                it?.let { currentValue ->
                    homeIncludeHeader.savingsLabelTotalExpensedQuantity.text =
                        currentValue.asCurrency(locale)
                }
            }
        }
    }

    private fun getCurrencyAnimated(endValue: Double, block: (currentFloat: Double?) -> Unit) {
        val animator = ValueAnimator.ofInt(1, 100)
        animator.duration = CURRENCY_ANIMATION
        animator.addUpdateListener {
            val value = (it.animatedValue as Int) * endValue / PERCENTAGE
            block(
                value.takeUnless { value.isNaN() }.orZero()
            )
        }
        animator.start()
    }

    companion object {
        const val SMOOTH_HORIZONTAL_SCROLL: Long = 1500
        const val ANIMATION_DELAY: Long = 1000
        const val SMOOTH_VERTICAL_SCROLL = 500
        const val PERCENTAGE = 100
        const val CURRENCY_ANIMATION: Long = 500
    }
}
