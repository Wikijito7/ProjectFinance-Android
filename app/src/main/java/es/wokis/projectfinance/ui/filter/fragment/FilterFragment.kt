package es.wokis.projectfinance.ui.filter.fragment

import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.databinding.FragmentFilterBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.filter.adapter.FilterTabAdapter
import es.wokis.projectfinance.ui.filter.viewmodel.FilterViewModel
import es.wokis.projectfinance.ui.home.fragment.HomeFragment
import es.wokis.projectfinance.utils.asCurrency
import es.wokis.projectfinance.utils.getLocale
import es.wokis.projectfinance.utils.getTintByContrast
import es.wokis.projectfinance.utils.getTotalDepositAsDouble
import es.wokis.projectfinance.utils.getTotalExpenseAsDouble
import es.wokis.projectfinance.utils.getTotalSavedAsDouble
import es.wokis.projectfinance.utils.showReinsertInvoiceSnackBar
import es.wokis.projectfinance.utils.toMonetaryDouble


class FilterFragment : BaseFragment() {

    private val viewModel: FilterViewModel by viewModels()

    private var binding: FragmentFilterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        setUpViewPagerAdapter()
        setUpTabLayout()
        setUpToolbar()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getInvoices()
        setUpObservers()
    }

    override fun getScreenName(): String = getString(R.string.title_filter)

    override fun showInvoiceRemovedSnackBar() {
        binding?.apply {
            showReinsertInvoiceSnackBar(root) {
                viewModel.reinsertRemovedInvoice()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
        hideBottomNavigation()
        interceptBackPressed {
            navigateBack()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun navigateBack() {
        viewModel.clearFilters()
        super.navigateBack()
    }

    private fun setUpObservers() {
        viewModel.getInvoicesLiveData().observe(viewLifecycleOwner) {
            binding?.apply {
                setUpHeader(it)
            }
        }
    }


    private fun FragmentFilterBinding.setUpHeader(invoices: List<InvoiceBO>) {
        context?.let { contextNotNull ->
            val locale = contextNotNull.getLocale()

            getCurrencyAnimated(invoices.getTotalSavedAsDouble().toMonetaryDouble()) {
                it?.let { currentValue ->
                    filterIncludeHeader.savingsLabelTotalSaved.text =
                        currentValue.asCurrency(locale)
                }
            }

            getCurrencyAnimated(invoices.getTotalDepositAsDouble().toMonetaryDouble()) {
                it?.let { currentValue ->
                    filterIncludeHeader.savingsLabelTotalDepositQuantity.text =
                        currentValue.asCurrency(locale)
                }
            }

            getCurrencyAnimated(invoices.getTotalExpenseAsDouble().toMonetaryDouble()) {
                it?.let { currentValue ->
                    filterIncludeHeader.savingsLabelTotalExpensedQuantity.text =
                        currentValue.asCurrency(locale)
                }
            }
        }
    }

    private fun getCurrencyAnimated(endValue: Double, block: (currentFloat: Double?) -> Unit) {
        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 750
        animator.addUpdateListener {
            block((it.animatedValue as Int) * endValue / HomeFragment.PERCENTAGE)
        }
        animator.start()
    }

    private fun setUpToolbar() {
        binding?.apply {
            val backIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_back, root.context.theme)
                    ?.apply {
                        context?.let {
                            (filterToolbarMainToolbar.background as? ColorDrawable)?.color?.let {
                                val color = getTintByContrast(it)
                                setTint(color)
                            }
                        }
                    }
            filterToolbarMainToolbar.navigationIcon = backIcon
            filterToolbarMainToolbar.setNavigationOnClickListener {
                navigateBack()
            }
        }
    }

    private fun setUpViewPagerAdapter() {
        binding?.apply {
            val tabsAdapter =
                FilterTabAdapter(
                    viewModel.getFilterTypes(),
                    this@FilterFragment
                )
            filterPagerMainPager.adapter = tabsAdapter
            filterPagerMainPager.isUserInputEnabled = false
        }
    }

    private fun setUpTabLayout() {
        binding?.apply {
            TabLayoutMediator(filterTabsFilteredInvoices, filterPagerMainPager) { tab, position ->
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