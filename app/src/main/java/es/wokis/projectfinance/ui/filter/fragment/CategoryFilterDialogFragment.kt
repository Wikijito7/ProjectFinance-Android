package es.wokis.projectfinance.ui.filter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_CATEGORIES_ID
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_CATEGORIES_NAME
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.DialogFilterCategoryBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.filter.adapter.FilterCategoryAdapter
import es.wokis.projectfinance.ui.filter.viewmodel.CategoryFilterViewModel

@AndroidEntryPoint
class CategoryFilterDialogFragment : BaseBottomSheetDialog() {

    private val viewModel: CategoryFilterViewModel by viewModels()
    private val args: CategoryFilterDialogFragmentArgs by navArgs()

    private var binding: DialogFilterCategoryBinding? = null
    private var adapter: FilterCategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFilterCategoryBinding.inflate(layoutInflater, container, false)
        setUpAdapter()
        setUpClickListener()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        viewModel.setSelectedIds(args.selectedIds)
        viewModel.getCategories()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpObservers() {
        setUpCategoriesObserver()
    }

    private fun setUpCategoriesObserver() {
        viewModel.getCategoriesLiveData().observe(viewLifecycleOwner) {
            adapter?.submitList(it)
            binding?.filterCategoryBtnAccept?.isEnabled = it.any { category -> category.selected }
        }
    }

    private fun setUpAdapter() {
        binding?.apply {
            adapter = FilterCategoryAdapter()
            adapter?.listener = {
                viewModel.updateSelectedCategories(it)
            }
            filterCategoryListCategories.adapter = adapter
            filterCategoryListCategories.itemAnimator?.apply {
                changeDuration = 0
                addDuration = 0
                moveDuration = 0
                removeDuration = 0
            }
        }
    }

    private fun setUpClickListener() {
        binding?.apply {
            filterCategoryBtnBack.setOnClickListener {
                navigateBack()
            }

            filterCategoryBtnAccept.setOnClickListener {
                onAccept()
            }
        }
    }

    private fun onAccept() {
        val categoriesId = viewModel.getIdOfSelectedCategories()
        val categoriesName = viewModel.getNameOfSelectedCategories()
        val bundle = Bundle().apply {
            putLongArray(FILTER_CATEGORIES_ID, categoriesId)
            putStringArray(FILTER_CATEGORIES_NAME, categoriesName)
        }
        setFragmentResult(RequestKeys.FILTER_TO_CATEGORY_FILTER, bundle)
        navigateBack()
    }

}