package es.wokis.projectfinance.ui.graphs.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.PieData
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.databinding.RowPieGraphTabBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.graphs.adapter.GraphsDataAdapter
import es.wokis.projectfinance.ui.graphs.fragment.GraphsFragment.Companion.GRAPH_TAB_TYPE
import es.wokis.projectfinance.ui.graphs.viewmodel.GraphsViewModel
import es.wokis.projectfinance.ui.graphs.vo.GraphDataVO
import es.wokis.projectfinance.ui.home.fragment.HomeFragment
import es.wokis.projectfinance.utils.getCategoryName
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.show

class PieGraphTabFragment : BaseFragment() {

    private val viewModel: GraphsViewModel by viewModels()

    private var binding: RowPieGraphTabBinding? = null
    private var tabType = EMPTY_TEXT
    private var adapter: GraphsDataAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RowPieGraphTabBinding.inflate(inflater, container, false)
        setUpAdapter()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabType = arguments?.getString(GRAPH_TAB_TYPE).orEmpty()
        viewModel.getInvoices()
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
        setUpInvoicesObserver()
    }

    private fun setUpInvoicesObserver() {
        viewModel.getInvoicesLiveData().observe(viewLifecycleOwner) {
            context?.let { contextNotNull ->
                it.map { invoice ->
                    invoice.copy(
                        category = invoice.category?.copy(
                            title = contextNotNull.getCategoryName(
                                invoice.category.title
                            )
                        )
                    )
                }.also {
                    setUpPieChart(it)
                }
            }
        }
    }

    private fun setUpPieChart(invoices: List<InvoiceBO>) {
        if (viewModel.isCategoryDeposit(tabType)) {
            setUpPieChart(viewModel.generateDepositPerCategoryPieData(invoices))
            submitAdapterData(viewModel.getDepositsGraphData(invoices))

        } else {
            setUpPieChart(viewModel.generateExpensesPerCategoryPieData(invoices))
            submitAdapterData(viewModel.getExpensesGraphData(invoices))
        }
    }

    private fun submitAdapterData(graphData: List<GraphDataVO>) {
        adapter?.submitList(graphData)
    }

    private fun setUpPieChart(chartData: PieData?) {
        if (chartData?.entryCount == 0) {
            showNoDataAvailable()
            return
        }
        binding?.apply {
            graphTabChartMovements.apply {
                setHoleColor(Color.TRANSPARENT)
                setTouchEnabled(false)
                legend.isEnabled = false
                description.isEnabled = false
                data = chartData
                animateXY(
                    HomeFragment.CURRENCY_ANIMATION.toInt(),
                    HomeFragment.CURRENCY_ANIMATION.toInt()
                )
                setDrawEntryLabels(false)
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

}