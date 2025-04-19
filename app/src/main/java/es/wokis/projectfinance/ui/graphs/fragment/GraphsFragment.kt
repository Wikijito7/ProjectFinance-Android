package es.wokis.projectfinance.ui.graphs.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import es.wokis.projectfinance.R
import es.wokis.projectfinance.databinding.FragmentGraphsBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.graphs.adapter.GraphsTabAdapter
import es.wokis.projectfinance.ui.graphs.viewmodel.GraphsViewModel
import es.wokis.projectfinance.utils.applyEdgeToEdge
import es.wokis.projectfinance.utils.getTintByContrast

class GraphsFragment : BaseFragment() {

    private val viewModel: GraphsViewModel by viewModels()

    private var binding: FragmentGraphsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGraphsBinding.inflate(inflater, container, false)
        setUpView()
        setUpToolbar()
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
        hideToolbar()
        hideAddInvoiceButton()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpView() {
        setUpViewPagerAdapter()
        setUpTabLayout()
    }

    private fun setUpViewPagerAdapter() {
        binding?.apply {
            val tabsAdapter =
                GraphsTabAdapter(
                    viewModel.getGraphDataTypes(),
                    viewModel.getGraphTypes(),
                    this@GraphsFragment
                )
            graphsPagerMainPager.adapter = tabsAdapter
        }
    }

    private fun setUpToolbar() {
        binding?.apply {
            val backIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_back, root.context.theme)
                    ?.apply {
                        context?.let {
                            (graphsToolbarMainToolbar.background as? ColorDrawable)?.color?.let {
                                val color = getTintByContrast(it)
                                setTint(color)
                            }
                        }
                    }
            graphsToolbarMainToolbar.navigationIcon = backIcon
            graphsToolbarMainToolbar.setNavigationOnClickListener {
                navigateBack()
            }
            root.applyEdgeToEdge(
                applyTopPadding = true,
                applyLeftPadding = true,
                applyRightPadding = true
            )
        }
    }

    private fun setUpTabLayout() {
        binding?.apply {
            TabLayoutMediator(graphsTabsInvoices, graphsPagerMainPager) { tab, position ->
                tab.text = getString(
                    when (position) {
                        0 -> R.string.home__historical_savings
                        1 -> R.string.home__deposit_category_pie_chart
                        else -> R.string.home__expense_category_pie_chart
                    }
                )
            }.attach()
        }
    }

    companion object {
        const val GRAPH_TAB_TYPE = "GRAPH_TAB_TYPE"
    }

}