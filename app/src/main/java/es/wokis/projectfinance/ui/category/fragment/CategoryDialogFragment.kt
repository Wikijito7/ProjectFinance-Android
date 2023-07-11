package es.wokis.projectfinance.ui.category.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.DialogAddCategoryBinding
import es.wokis.projectfinance.ui.addinvoice.fragment.AddInvoiceDialogFragment.Companion.CATEGORY_ID
import es.wokis.projectfinance.ui.addinvoice.fragment.AddInvoiceDialogFragment.Companion.CATEGORY_NAME
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.category.adapter.CategoryAdapter
import es.wokis.projectfinance.ui.category.viewmodel.CategoryViewModel
import es.wokis.projectfinance.utils.RecyclerSwipeHelper
import es.wokis.projectfinance.utils.SwipeButton
import es.wokis.projectfinance.utils.hideKeyboard
import es.wokis.projectfinance.utils.showSnackBar

@AndroidEntryPoint
class CategoryDialogFragment : BaseBottomSheetDialog() {

    private var binding: DialogAddCategoryBinding? = null

    private val viewModel by viewModels<CategoryViewModel>()
    private val args: CategoryDialogFragmentArgs by navArgs()

    private var adapter: CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAddCategoryBinding.inflate(inflater, container, false)
        extendView()
        setUpDialog()
        setUpAdapter()
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedCategoryId = args.categoryId
        viewModel.getCategories()
        setUpObservers()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpAdapter() {
        binding?.apply {
            context?.let {
                setUpSwipeHelper(it)
            }
            adapter = CategoryAdapter()
            adapter?.listener = {
                binding?.addCategoryBtnAccept?.isEnabled = true
                viewModel.onCategoryClick(it)
            }
            addCategoryListCategories.adapter = adapter
            addCategoryListCategories.itemAnimator?.apply {
                changeDuration = 0
                addDuration = 0
                moveDuration = 0
                removeDuration = 0
            }
        }
    }

    private fun setUpSwipeHelper(context: Context) {
        val deleteColor =
            ResourcesCompat.getColor(context.resources, R.color.red, context.theme)
        val editColor =
            ResourcesCompat.getColor(context.resources, R.color.light_orange, context.theme)
        val editIcon =
            ResourcesCompat.getDrawable(context.resources, R.drawable.ic_edit_swipe, context.theme)
        val deleteIcon =
            ResourcesCompat.getDrawable(context.resources, R.drawable.ic_delete_swipe, context.theme)
        val swipeHelper = ItemTouchHelper(
            RecyclerSwipeHelper(
                SwipeButton(deleteColor, deleteIcon) {
                    viewModel.deleteCategory(it)
                },
                SwipeButton(editColor, editIcon) {
                    viewModel.editCategory(it)
                }
            )
        )
        binding?.apply {
            swipeHelper.attachToRecyclerView(addCategoryListCategories)
        }
    }

    private fun setUpListeners() {
        binding?.apply {
            addCategoryBtnAccept.setOnClickListener {
                onAccept()
            }

            addCategoryBtnBack.setOnClickListener {
                navigateBack()
            }

            addCategoryBtnAddCategory.setOnClickListener {
                navigateToCreateCategory()
            }
        }
    }

    private fun navigateToCreateCategory(id: Long = DEFAULT_CATEGORY_ID) {
        navigateTo(
            CategoryDialogFragmentDirections.actionCategoryToCreateCategory(
                id
            )
        )
    }

    private fun onAccept() {
        goBack()
        binding?.root?.let { hideKeyboard(it) }
    }

    private fun setUpObservers() {
        observeGetCategories()
        observeEditCategory()
        observeCategoryDeleted()
    }

    private fun observeCategoryDeleted() {
        viewModel.getCategoryDeletedLiveData().observe(viewLifecycleOwner) {
            binding?.apply {
                showSnackBar(
                    R.string.add_category__category_deleted,
                    R.string.general__revert,
                    root,
                    action = {
                        viewModel.reinsertCategory()
                    },
                    onDismissed = {
                        viewModel.checkInvoicesWithoutCategory()
                    }
                )
            }
        }
    }

    private fun observeEditCategory() {
        viewModel.getEditCategoryLiveData().observe(viewLifecycleOwner) {
            navigateToCreateCategory(it)
        }
    }

    private fun observeGetCategories() {
        viewModel.getCategoriesLiveData().observe(viewLifecycleOwner) {
            binding?.addCategoryBtnAccept?.isEnabled = it.any { category -> category.selected }
            adapter?.submitList(it)
        }
    }

    private fun goBack() {
        val categorySelected = viewModel.categorySelected
        categorySelected?.let {
            val bundle = Bundle().apply {
                putLong(CATEGORY_ID, it.id)
                putString(CATEGORY_NAME, it.title)
            }
            setFragmentResult(RequestKeys.ADD_INVOICE_TO_CATEGORY, bundle)
        }
        navigateBack()
    }

    companion object {
        const val DEFAULT_CATEGORY_ID = 0L
    }
}