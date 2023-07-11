package es.wokis.projectfinance.ui.graphs.fragment

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.databinding.RowLineGraphTabBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.graphs.adapter.GraphsDataAdapter
import es.wokis.projectfinance.ui.graphs.fragment.GraphsFragment.Companion.GRAPH_TAB_TYPE
import es.wokis.projectfinance.ui.graphs.viewmodel.GraphsViewModel
import es.wokis.projectfinance.ui.home.fragment.HomeFragment
import es.wokis.projectfinance.utils.getLocale
import es.wokis.projectfinance.utils.getPrimaryColor
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.orZero
import es.wokis.projectfinance.utils.show

class LineGraphTabFragment : BaseFragment() {

    private val viewModel: GraphsViewModel by viewModels()

    private var binding: RowLineGraphTabBinding? = null
    private var tabType = EMPTY_TEXT
    private var adapter: GraphsDataAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RowLineGraphTabBinding.inflate(inflater, container, false)
        setUpAdapter()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabType = arguments?.getString(GRAPH_TAB_TYPE).orEmpty()
        viewModel.getAllInvoices()
        setUpObservers()
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
        hideAddInvoiceButton()
        hideToolbar()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpAdapter() {
        binding?.apply {
            adapter = GraphsDataAdapter()
            graphTabListData.adapter = adapter
        }
    }

    private fun setUpObservers() {
        setUpAllInvoicesObserver()
    }

    private fun setUpAllInvoicesObserver() {
        viewModel.getAllInvoicesLiveData().observe(viewLifecycleOwner) {
            setUpBarChart(it)
            context?.let { context ->
                adapter?.submitList(viewModel.getHistoricalGraphData(it, context.getLocale()))
            }
        }
    }

    private fun setUpBarChart(invoices: List<InvoiceBO>) {
        context?.let {
            val customColor = it.getTextColor()
            binding?.graphTabChartMovements?.apply {
                val drawColor = it.theme.getPrimaryColor() ?: ResourcesCompat.getColor(
                    it.resources,
                    R.color.orange,
                    it.theme
                )
                val chartData = viewModel.generateSavingsHistoricalData(invoices, drawColor)
                if (chartData?.entryCount.orZero() < 2) {
                    showNoDataAvailable()
                    return
                }
                data = chartData
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                xAxis.apply {
                    granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return viewModel.getDisplayDates(invoices)[value.toInt()]
                        }
                    }
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = customColor
                    textSize = 12f
                    setExtraOffsets(0f, 0f, 20f, 12f)
                }
                axisLeft.apply {
                    textColor = customColor
                    granularity = 100f
                    textSize = 12f
                    valueFormatter = LargeValueFormatter()
                }
                animateXY(
                    HomeFragment.CURRENCY_ANIMATION.toInt(),
                    HomeFragment.CURRENCY_ANIMATION.toInt()
                )
                setTouchEnabled(false)
            }
        }
    }

    private fun showNoDataAvailable() {
        binding?.apply {
            graphTabChartMovements.hide()
            graphTabListData.hide()
            graphTabLabelNoData.show()
        }
    }

    private fun Context.getTextColor(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        return typedValue.data
    }

}