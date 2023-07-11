package es.wokis.projectfinance.ui.addinvoice.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.constants.AppConstants.INSERTED
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.DialogAddInvoiceBinding
import es.wokis.projectfinance.ui.addinvoice.filter.InvoiceAmountFilter
import es.wokis.projectfinance.ui.addinvoice.viewmodel.AddInvoiceViewModel
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.utils.getCategoryName
import es.wokis.projectfinance.utils.getDateFormatted
import es.wokis.projectfinance.utils.hideKeyboard
import es.wokis.projectfinance.utils.orZero
import es.wokis.projectfinance.utils.show
import es.wokis.projectfinance.utils.toCents
import es.wokis.projectfinance.utils.toMonetaryFloat
import es.wokis.projectfinance.utils.toStringFormatted
import kotlin.math.absoluteValue

@AndroidEntryPoint
class AddInvoiceDialogFragment : BaseBottomSheetDialog() {

    private var binding: DialogAddInvoiceBinding? = null

    private val viewModel by viewModels<AddInvoiceViewModel>()
    private val args: AddInvoiceDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogAddInvoiceBinding.inflate(inflater, container, false)
        extendView()
        setUpDialog()
        setUpView()
        setUpListeners()
        return binding?.root
    }

    private fun setUpView() {
        binding?.addInvoiceInputAmount?.filters = arrayOf(InvoiceAmountFilter())
        binding?.addInvoiceInputDate?.setText(viewModel.getTodayDate())
        args.invoiceType.takeIf { it != GET_FROM_INVOICE }?.let {
            updateView(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFragmentResultListener()
        setUpObservers()
        if (args.invoiceId != 0L) {
            viewModel.getInvoice(args.invoiceId)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun updateView(invoiceTypeKey: String) {
        val invoiceType = InvoiceType.getFromKey(invoiceTypeKey)
        val title = getString(
            when (invoiceType) {
                InvoiceType.EXPENSE -> if (args.invoiceId == 0L) {
                    R.string.add_new_expense

                } else {
                    R.string.edit_expense
                }

                else -> if (args.invoiceId == 0L) {
                    R.string.add_new_deposit

                } else {
                    R.string.edit_expense
                }
            }
        )
        viewModel.invoiceType = invoiceType
        binding?.apply {
            addInvoiceLabelTitle.text = title
            addInvoiceInputInvoiceType.setText(
                getString(
                    if (invoiceType == InvoiceType.DEPOSIT) {
                        R.string.add_invoice__deposit

                    } else {
                        R.string.add_invoice__expense

                    }
                )
            )
        }
    }

    private fun setUpFragmentResultListener() {
        setUpCategoryListener()
        setUpDatePickerListener()
        setUpInvoiceTypeListener()
        setUpSyncListener()
    }

    private fun setUpInvoiceTypeListener() {
        setFragmentResultListener(RequestKeys.ADD_INVOICE_TO_EDIT_INVOICE_TYPE) { _, bundle ->
            val invoiceType = bundle.getString(INVOICE_TYPE).orEmpty()
            updateView(invoiceType)
        }
    }

    private fun setUpCategoryListener() {
        setFragmentResultListener(RequestKeys.ADD_INVOICE_TO_CATEGORY) { _, bundle ->
            val categoryId = bundle.getLong(CATEGORY_ID)
            val categoryName = bundle.getString(CATEGORY_NAME)
            updateCategory(categoryId, categoryName)
        }
    }

    private fun setUpSyncListener() {
        setFragmentResultListener(RequestKeys.ADD_INVOICE_TO_SYNC) { _, bundle ->
            val shouldSync = bundle.getBoolean(SHOULD_SYNC)
            updateSync(shouldSync)
        }
    }

    private fun updateSync(shouldSync: Boolean) {
        binding?.addInvoiceInputSync?.setText(
            getString(
                if (shouldSync) {
                    R.string.add_invoice__sync_on

                } else {
                    R.string.add_invoice__sync_off
                }
            )
        )
    }

    private fun updateCategory(categoryId: Long, categoryName: String?) {
        binding?.addInvoiceInputCategory?.setText(
            context.getCategoryName(categoryName)
        )
        viewModel.saveCategoryId(categoryId)
    }

    private fun setUpDatePickerListener() {
        setFragmentResultListener(RequestKeys.ADD_INVOICE_TO_DATE_PICKER) { _, bundle ->
            val day = bundle.getInt(DAY)
            val month = bundle.getInt(MONTH)
            val year = bundle.getInt(YEAR)
            updateDate(day, month, year)
        }
    }

    private fun updateDate(day: Int, month: Int, year: Int) {
        binding?.addInvoiceInputDate?.setText(getDateFormatted(day, month, year))
    }

    private fun setUpListeners() {
        binding?.apply {
            addInvoiceBtnAccept.setOnClickListener {
                onAccept()
            }

            addInvoiceBtnClose.setOnClickListener {
                hideKeyboard(it)
                navigateBack()
            }

            addInvoiceInputDate.setOnClickListener {
                openDatePicker()
            }

            addInvoiceInputCategory.setOnClickListener {
                navigateTo(AddInvoiceDialogFragmentDirections.actionAddInvoiceToCategory(viewModel.getCategoryId()))
            }

            addInvoiceInputSync.setOnClickListener {
                navigateTo(
                    AddInvoiceDialogFragmentDirections.actionAddInvoiceToCloudSynchronization(
                        shouldSynchronize()
                    )
                )
            }

            addInvoiceInputInvoiceType.setOnClickListener {
                navigateTo(
                    AddInvoiceDialogFragmentDirections.actionAddInvoiceToEditInvoiceType(
                        viewModel.invoiceType.key
                    )
                )
            }

            addInvoiceInputDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // no-op
                }

                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding?.apply {
                        addInvoiceLabelDescriptionCharacters.show()
                        addInvoiceLabelDescriptionCharacters.text =
                            (MAX_DESCRIPTION_CHARACTERS - text?.toString()?.length.orZero()).toString()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    // no-op
                }

            })
            onTextChanged(addInvoiceInputTitle)
            onTextChanged(addInvoiceInputAmount)
            onTextChanged(addInvoiceInputDate)
        }
    }

    private fun onTextChanged(v: EditText) {
        v.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no-op
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding?.addInvoiceBtnAccept?.isEnabled =
                    getTitle().isNotBlank() && getInvoiceAmount().isNotBlank() && getDate().isNotBlank()
            }

            override fun afterTextChanged(p0: Editable?) {
                // no-op
            }

        })
    }

    private fun openDatePicker() {
        navigateTo(AddInvoiceDialogFragmentDirections.actionAddInvoiceToDatePicker(RequestKeys.ADD_INVOICE_TO_DATE_PICKER))
    }

    private fun onAccept() {
        if (isAllValid()) {
            viewModel.addInvoice(
                args.invoiceId,
                getTitle(),
                getDescription(),
                getInvoiceAmount().toFloatOrNull().orZero(),
                getDate(),
                shouldSynchronize()
            )
        }
        binding?.root?.let { hideKeyboard(it) }
    }

    private fun isAllValid(): Boolean {
        binding?.apply {
            if (getTitle().isBlank()) {
                addInvoiceInputTitle.error = getString(R.string.add_invoice__title_empty_error)
                return false

            } else {
                addInvoiceInputTitle.error = null
            }

            if (getInvoiceAmount().toFloatOrNull().orZero() == 0f) {
                addInvoiceInputAmount.error =
                    getString(R.string.add_invoice__amount_equal_zero_error)
                return false

            } else if (getInvoiceAmount().toFloatOrNull()?.toCents()
                    .orZero().absoluteValue > Integer.MAX_VALUE - 1
            ) {
                addInvoiceInputAmount.error =
                    getString(R.string.add_invoice__amount_too_many_characters_error)
                return false

            } else {
                addInvoiceInputAmount.error = null
            }

            if (getDate().isBlank()) {
                addInvoiceInputDate.error = getString(R.string.add_invoice__date_empty_error)
                return false

            } else {
                addInvoiceInputDate.error = null
            }
            return true
        }
        return false
    }

    private fun getTitle() = binding?.addInvoiceInputTitle?.text.toString()

    private fun getDescription() = binding?.addInvoiceInputDescription?.text.toString()

    private fun getInvoiceAmount() = binding?.addInvoiceInputAmount?.text.toString()

    private fun getDate() = binding?.addInvoiceInputDate?.text.toString()

    private fun shouldSynchronize() =
        binding?.addInvoiceInputSync?.text.toString() == getString(R.string.add_invoice__sync_on)

    private fun setUpObservers() {
        observeAddInvoiceResult()
        observeGetInvoice()
    }

    private fun observeGetInvoice() {
        viewModel.getInvoiceLiveData().observe(viewLifecycleOwner) {
            setUpView(it)
        }
    }

    private fun setUpView(invoice: InvoiceBO) {
        binding?.apply {
            addInvoiceInputTitle.setText(invoice.title)
            addInvoiceInputDescription.setText(invoice.description)
            addInvoiceInputDate.setText(invoice.date.toStringFormatted())
            addInvoiceInputAmount.setText(invoice.quantity.toMonetaryFloat().absoluteValue.toString())
            addInvoiceInputCategory.setText(context.getCategoryName(invoice.category?.title))
            addInvoiceInputSync.setText(
                getText(
                    if (invoice.synchronize) {
                        R.string.add_invoice__sync_on

                    } else {
                        R.string.add_invoice__sync_off
                    }
                )
            )
            updateView(invoice.type.key)
        }
    }

    private fun observeAddInvoiceResult() {
        viewModel.getAddInvoiceResultLiveData().observe(viewLifecycleOwner) {
            goBack(it)
        }
    }

    private fun goBack(inserted: Boolean) {
        val bundle = Bundle().apply {
            putBoolean(INSERTED, inserted)
        }
        setFragmentResult(RequestKeys.UPDATE_INVOICE_DETAIL, bundle)
        navigateBack()
    }

    companion object {
        const val DAY = "DAY"
        const val MONTH = "MONTH"
        const val YEAR = "YEAR"
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SHOULD_SYNC = "SHOULD_SYNC"
        const val CATEGORY_NAME = "CATEGORY_NAME"
        const val INVOICE_TYPE = "INVOICE_TYPE"
        private const val MAX_DESCRIPTION_CHARACTERS = 200
        private const val GET_FROM_INVOICE = "GET_FROM_INVOICE"
    }
}