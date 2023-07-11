package es.wokis.projectfinance.ui.addinvoice.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.DialogEditInvoiceTypeBinding
import es.wokis.projectfinance.ui.addinvoice.fragment.AddInvoiceDialogFragment.Companion.INVOICE_TYPE
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog

class EditInvoiceTypeDialogFragment : BaseBottomSheetDialog() {
    private val args: EditInvoiceTypeDialogFragmentArgs by navArgs()

    private var binding: DialogEditInvoiceTypeBinding? = null
    private var invoiceTypeSelected: InvoiceType = InvoiceType.DEPOSIT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditInvoiceTypeBinding.inflate(inflater, container, false)
        setUpDialog()
        setUpView()
        setUpListeners()
        return binding?.root
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun setUpView() {
        val invoiceType = InvoiceType.getFromKey(args.invoiceType)
        updateTypeSelected(invoiceType)
    }

    private fun setUpListeners() {
        binding?.apply {
            editInvoiceTypeContainerDeposit.setOnClickListener {
                updateTypeSelected(InvoiceType.DEPOSIT)
            }

            editInvoiceTypeContainerExpense.setOnClickListener {
                updateTypeSelected(InvoiceType.EXPENSE)
            }

            editInvoiceTypeBtnAccept.setOnClickListener {
                onAccept()
            }

            editInvoiceTypeBtnClose.setOnClickListener {
                navigateBack()
            }
        }
    }

    private fun onAccept() {
        val bundle = Bundle().apply {
            putString(INVOICE_TYPE, invoiceTypeSelected.key)
        }
        setFragmentResult(RequestKeys.ADD_INVOICE_TO_EDIT_INVOICE_TYPE, bundle)
        navigateBack()
    }

    private fun updateTypeSelected(type: InvoiceType) {
        invoiceTypeSelected = type
        binding?.apply {
            editInvoiceTypeRadioDeposit.isChecked = invoiceTypeSelected == InvoiceType.DEPOSIT
            editInvoiceTypeRadioExpense.isChecked = invoiceTypeSelected == InvoiceType.EXPENSE
        }
    }
}