package es.wokis.projectfinance.ui.detail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.constants.AppConstants
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.data.enums.DetailOptionsEnum
import es.wokis.projectfinance.databinding.DialogInvoiceDetailBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.detail.viewmodel.InvoiceDetailViewModel
import es.wokis.projectfinance.utils.asCurrency
import es.wokis.projectfinance.utils.getLocale
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.show
import es.wokis.projectfinance.utils.toMonetaryFloat
import es.wokis.projectfinance.utils.toStringFormatted

@AndroidEntryPoint
class InvoiceDetailDialogFragment : BaseBottomSheetDialog() {

    private var binding: DialogInvoiceDetailBinding? = null

    private val viewModel by viewModels<InvoiceDetailViewModel>()
    private val args: InvoiceDetailDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInvoiceDetailBinding.inflate(inflater, container, false)
        setUpDialog()
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setUpFragmentResultListener()
        viewModel.getInvoice(args.invoiceId)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpFragmentResultListener() {
        setFragmentResultListener(RequestKeys.UPDATE_INVOICE_DETAIL) { _, bundle ->
            val inserted = bundle.getBoolean(AppConstants.INSERTED)
            updateView(inserted)
        }

        setFragmentResultListener(RequestKeys.DETAIL_OPTION_SELECTED) { _, bundle ->
            val optionSelected = bundle.getString(OPTION_SELECTED).orEmpty()
            DetailOptionsEnum.getFromKey(optionSelected)?.let {
                onOptionSelected(it)
            }
        }
    }

    private fun updateView(inserted: Boolean) {
        if (inserted) {
            viewModel.getInvoice(args.invoiceId)
        }
    }

    private fun setUpListeners() {
        binding?.apply {
            detailBtnBack.setOnClickListener {
                navigateBack()
            }

            detailBtnOptions.setOnClickListener {
                navigateTo(InvoiceDetailDialogFragmentDirections.actionDetailToDetailOptions())
            }
        }
    }

    private fun onOptionSelected(optionSelected: DetailOptionsEnum) {
        when (optionSelected) {
            DetailOptionsEnum.EDIT_INVOICE -> {
                viewModel.navigateToEdit(args.invoiceId)
            }

            DetailOptionsEnum.REMOVE_INVOICE -> {
                viewModel.removeInvoice()
            }

            DetailOptionsEnum.ADD_REACTION -> {
                viewModel.navigateToEmojiSelector(args.invoiceId)
            }
        }
    }

    private fun setUpObservers() {
        observeGetInvoiceResult()
        observeIsInvoiceRemoved()
        observeNavigateToEditInvoice()
    }

    private fun observeNavigateToEditInvoice() {
        viewModel.getNavigateToEditLiveData().observe(viewLifecycleOwner) {
            navigateTo(InvoiceDetailDialogFragmentDirections.actionDetailToAddInvoice(args.invoiceId))
        }
    }

    private fun observeIsInvoiceRemoved() {
        viewModel.getIsInvoiceRemovedLiveData().observe(viewLifecycleOwner) { removed ->
            if (removed) {
                onInvoiceRemoved()
            }
        }
    }

    private fun onInvoiceRemoved() {
        setFragmentResult(RequestKeys.DETAIL_REMOVE_INVOICE, bundleOf())
        navigateBack()
    }

    private fun observeGetInvoiceResult() {
        viewModel.getInvoiceLiveData().observe(viewLifecycleOwner) {
            setUpView(it)
        }
    }

    private fun setUpView(invoice: InvoiceBO) {
        binding?.apply {
            detailLabelInvoiceTitle.text = invoice.title
            detailLabelInvoiceDate.text = invoice.date.toStringFormatted()
            detailLabelInvoiceAmount.text =
                context?.getLocale()?.let { invoice.quantity.toMonetaryFloat().asCurrency(it) }
            detailLabelSync.text = getString(
                when {
                    invoice.synchronize.not() -> R.string.detail__sync_never
                    invoice.serverId == null || invoice.updated -> R.string.detail__sync_off
                    else -> R.string.detail__sync_on
                }
            )
            invoice.description.takeIf { it.isNotBlank() }?.let {
                detailLabelInvoiceDescription.text = it
                detailLabelInvoiceDescriptionTile.show()
                detailLabelInvoiceDescription.show()
            } ?: run {
                detailLabelInvoiceDescriptionTile.hide()
                detailLabelInvoiceDescription.hide()
            }
            invoice.category?.let {
                detailLabelInvoiceCategory.text = when (it.title) {
                    AppConstants.DEFAULT_CATEGORY -> context?.getString(R.string.category__none)
                    else -> it.title
                }
                detailLabelInvoiceCategoryTile.show()
                detailLabelInvoiceCategory.show()
            } ?: run {
                detailLabelInvoiceCategoryTile.hide()
                detailLabelInvoiceCategory.hide()
            }
        }
    }

    companion object {
        const val OPTION_SELECTED = "OPTION_SELECTED"
    }
}