package es.wokis.projectfinance.ui.filter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.constants.AppConstants.CATEGORIES_SEPARATOR
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_CATEGORIES_ID
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_CATEGORIES_NAME
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_DATE_FROM
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_DATE_TO
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.DialogSelectFilterBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.filter.viewmodel.SelectFilterViewModel
import es.wokis.projectfinance.utils.getCategoryName
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.show

@AndroidEntryPoint
class SelectFilterDialogFragment : BaseBottomSheetDialog() {

    private val viewModel: SelectFilterViewModel by viewModels()

    private var binding: DialogSelectFilterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSelectFilterBinding.inflate(layoutInflater, container, false)
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFragmentResultListener()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpFragmentResultListener() {
        setUpCategoryListener()
        setUpDateListener()
    }

    private fun setUpCategoryListener() {
        setFragmentResultListener(RequestKeys.FILTER_TO_CATEGORY_FILTER) { _, bundle ->
            val categoriesId = bundle.getLongArray(FILTER_CATEGORIES_ID)
            val categoriesName = bundle.getStringArray(FILTER_CATEGORIES_NAME)
            saveCategoriesSelected(categoriesId, categoriesName)
        }
    }

    private fun saveCategoriesSelected(categoriesId: LongArray?, categoriesName: Array<String>?) {
        viewModel.saveCategoriesSelected(categoriesId, categoriesName)
        binding?.apply {
            selectFilterLabelCategory.apply {
                text = getString(
                    R.string.filter__categories_selected,
                    categoriesName?.joinToString(CATEGORIES_SEPARATOR) {
                        root.context.getCategoryName(
                            it
                        )
                    }
                )
                show()
            }
            selectFilterBtnDeleteCategoryFilter.show()
        }
    }

    private fun setUpDateListener() {
        setFragmentResultListener(RequestKeys.FILTER_TO_DATE_FILTER) { _, bundle ->
            val dateFrom = bundle.getString(FILTER_DATE_FROM)
            val dateTo = bundle.getString(FILTER_DATE_TO)
            saveDatesSelected(dateFrom, dateTo)
        }
    }

    private fun saveDatesSelected(dateFrom: String?, dateTo: String?) {
        viewModel.saveDatesSelected(dateFrom, dateTo)
        val dateString = dateTo?.takeIf { it.isNotBlank() }?.let {
            getString(R.string.filter__date_from_to, dateFrom, dateTo)
        } ?: getString(R.string.filter__date_from, dateFrom)
        binding?.apply {
            selectFilterLabelDate.apply {
                text = dateString
                show()
            }
            selectFilterBtnDeleteDateFilter.show()
        }
    }

    private fun setUpListeners() {
        binding?.apply {
            selectFilterBtnClose.setOnClickListener {
                navigateBack()
            }

            selectFilterBtnCategory.setOnClickListener {
                navigateToCategoryFilter()
            }

            selectFilterLabelCategory.setOnClickListener {
                navigateToCategoryFilter()
            }

            selectFilterBtnDate.setOnClickListener {
                navigateToDateFilter()
            }

            selectFilterLabelDate.setOnClickListener {
                navigateToDateFilter()
            }

            selectFilterBtnDeleteCategoryFilter.setOnClickListener {
                onDeleteCategoryFilter()
            }

            selectFilterBtnDeleteDateFilter.setOnClickListener {
                onDeleteDateFilter()
            }

            selectFilterBtnAccept.setOnClickListener {
                onAccept()
            }
        }
    }

    private fun navigateToDateFilter() {
        navigateTo(
            SelectFilterDialogFragmentDirections.actionSelectFilterToDateFilter(
                viewModel.getDateFrom(),
                viewModel.getDateTo()
            )
        )
    }

    private fun navigateToCategoryFilter() {
        navigateTo(
            SelectFilterDialogFragmentDirections.actionSelectFilterToCategoryFilter(
                viewModel.getSelectedCategoriesId()
            )
        )
    }

    private fun onDeleteCategoryFilter() {
        viewModel.deleteCategoryFilters()
        binding?.apply {
            selectFilterBtnDeleteCategoryFilter.hide()
            selectFilterLabelCategory.hide()
        }
    }

    private fun onDeleteDateFilter() {
        viewModel.deleteDateFilters()
        binding?.apply {
            selectFilterBtnDeleteDateFilter.hide()
            selectFilterLabelDate.hide()
        }
    }

    private fun onAccept() {
        viewModel.saveFilters()
        navigateTo(SelectFilterDialogFragmentDirections.actionSelectFilterToFilter())
    }
}