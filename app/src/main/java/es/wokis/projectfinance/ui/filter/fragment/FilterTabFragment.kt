package es.wokis.projectfinance.ui.filter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import es.wokis.projectfinance.R
import es.wokis.projectfinance.databinding.RowFilterTabBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.dashboard.adapter.DashboardInvoicesAdapter
import es.wokis.projectfinance.ui.filter.viewmodel.FilterViewModel
import es.wokis.projectfinance.utils.RecyclerSwipeHelper
import es.wokis.projectfinance.utils.SwipeButton
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.showReinsertInvoiceSnackBar

class FilterTabFragment : BaseFragment() {

    private var binding: RowFilterTabBinding? = null

    private val viewModel by viewModels<FilterViewModel>()

    private var adapter: DashboardInvoicesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RowFilterTabBinding.inflate(inflater, container, false)
        setUpAdapter()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        val tabType = arguments?.getString(TAB_TYPE).orEmpty()
        viewModel.getInvoices(tabType)
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
        hideBottomNavigation()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun showInvoiceRemovedSnackBar() {
        // no-op
    }

    private fun setUpAdapter() {
        adapter = DashboardInvoicesAdapter()
        adapter?.setOnClickListener {
            navigateTo(FilterFragmentDirections.actionFilterToDetail(it))
        }
        binding?.rowFilterListInvoices?.apply {
            adapter = this@FilterTabFragment.adapter
            setUpSwipeHelper()
        }
    }

    private fun setUpSwipeHelper() {
        context?.let {
            val deleteColor =
                ResourcesCompat.getColor(it.resources, R.color.red, it.theme)
            val editColor =
                ResourcesCompat.getColor(it.resources, R.color.light_green, it.theme)
            val editIcon =
                ResourcesCompat.getDrawable(it.resources, R.drawable.ic_edit_swipe, it.theme)
            val deleteIcon =
                ResourcesCompat.getDrawable(it.resources, R.drawable.ic_delete_swipe, it.theme)
            val swipeHelper = ItemTouchHelper(
                RecyclerSwipeHelper(
                    SwipeButton(deleteColor, deleteIcon) { position ->
                        viewModel.deleteInvoice(position)
                    },
                    SwipeButton(editColor, editIcon) { position ->
                        viewModel.editInvoice(position)
                    }
                )
            )
            binding?.apply {
                swipeHelper.attachToRecyclerView(rowFilterListInvoices)
            }
        }
    }

    private fun setUpObservers() {
        observeInvoices()
        observeDeleteInvoice()
        observeEditInvoice()
    }

    private fun observeEditInvoice() {
        viewModel.getEditInvoiceLiveData().observe(viewLifecycleOwner) {
            navigateTo(FilterFragmentDirections.actionFilterToAddInvoice(it, GET_FROM_INVOICE))
        }
    }

    private fun observeDeleteInvoice() {
        viewModel.getDeleteInvoiceLiveData().observe(viewLifecycleOwner) {
            binding?.apply {
                showReinsertInvoiceSnackBar(root) {
                    viewModel.reinsertRemovedInvoice()
                }
            }
        }
    }

    private fun observeInvoices() {
        viewModel.getInvoicesVOLiveData().observe(viewLifecycleOwner) {
            binding?.rowFilterListInvoices.setVisible(it.isNotEmpty()) {
                adapter?.setInvoices(it)
            }
            binding?.rowFilterLabelNoContent.setVisible(it.isEmpty())
        }
    }

    companion object {
        const val TAB_TYPE = "TAB_TYPE"
        private const val GET_FROM_INVOICE = "GET_FROM_INVOICE"
    }
}