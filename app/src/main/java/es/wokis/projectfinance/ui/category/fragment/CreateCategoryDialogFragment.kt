package es.wokis.projectfinance.ui.category.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.DialogCreateCategoryBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.category.viewmodel.CreateCategoryViewModel
import es.wokis.projectfinance.utils.hideKeyboard

@AndroidEntryPoint
class CreateCategoryDialogFragment : BaseBottomSheetDialog() {

    private var binding: DialogCreateCategoryBinding? = null

    private val viewModel by viewModels<CreateCategoryViewModel>()
    private val args: CreateCategoryDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCreateCategoryBinding.inflate(inflater, container, false)
        extendView()
        setUpDialog()
        setUpView()
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.id.takeIf { it > 0L }?.let {
            viewModel.getCategoryById(it)
            setUpEditCategoryView()
        }
        setUpObservers()
        setUpFragmentResultListener()
    }

    private fun setUpEditCategoryView() {
        binding?.apply {
            createCategoryLabelTitle.text = getString(R.string.create_category__edit_category)
            createCategoryBtnAccept.text = getString(R.string.create_category__accept_edit_category)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpView() {
        binding?.categoryImgColor?.drawable?.setTint(
            ResourcesCompat.getColor(
                resources,
                R.color.transparent,
                context?.theme
            )
        )
    }

    private fun setUpFragmentResultListener() {
        setFragmentResultListener(RequestKeys.CREATE_CATEGORY_TO_COLOR) { _, bundle ->
            val color = bundle.getString(COLOR_KEY)
            updateColorValue(color.orEmpty())
        }
    }

    private fun updateColorValue(color: String) {
        binding?.apply {
            createCategoryInputColor.setText(color)
            categoryImgColor.drawable.setTint(Color.parseColor(color))
        }
    }

    private fun setUpListeners() {
        binding?.apply {
            createCategoryBtnAccept.setOnClickListener {
                onAccept()
            }

            createCategoryBtnClose.setOnClickListener {
                navigateBack()
            }

            createCategoryInputColor.setOnClickListener {
                navigateToColorPicker()
            }

            onTextChanged(createCategoryInputName)
            onTextChanged(createCategoryInputColor)
        }
    }

    private fun onTextChanged(v: EditText) {
        v.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no-op
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding?.createCategoryBtnAccept?.isEnabled = getTitle().isNotBlank() &&
                        getColor().isNotBlank()
            }

            override fun afterTextChanged(p0: Editable?) {
                // no-op
            }

        })
    }

    private fun getColor(): String = binding?.createCategoryInputColor?.text?.toString().orEmpty()

    private fun getTitle(): String = binding?.createCategoryInputName?.text?.toString().orEmpty()

    private fun navigateToColorPicker(defaultColor: String = DEFAULT_COLOR) {
        navigateTo(CreateCategoryDialogFragmentDirections.actionCreateCategoryToColorPicker(defaultColor))
    }

    private fun onAccept() {
        viewModel.addCategory(getTitle(), getColor())
        binding?.root?.let { hideKeyboard(it) }
    }

    private fun setUpObservers() {
        observeAddInvoiceResult()
        observeGetCategoryById()
    }

    private fun observeGetCategoryById() {
        viewModel.getCategoriesLiveData().observe(viewLifecycleOwner) {
            setUpCategoryData(it)
        }
    }

    private fun setUpCategoryData(category: CategoryBO) {
        binding?.apply {
            createCategoryInputName.setText(category.title)
            updateColorValue(category.color)

            createCategoryInputColor.setOnClickListener {
                navigateToColorPicker(category.color)
            }
        }
    }

    private fun observeAddInvoiceResult() {
        viewModel.getInsertedCategoryLiveData().observe(viewLifecycleOwner) {
            navigateBack()
        }
    }

    companion object {
        const val COLOR_KEY = "COLOR_KEY"
        private const val DEFAULT_COLOR = "#FFFFFF"
    }
}