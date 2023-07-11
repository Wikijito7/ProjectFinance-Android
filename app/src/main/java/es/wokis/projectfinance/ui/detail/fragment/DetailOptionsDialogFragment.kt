package es.wokis.projectfinance.ui.detail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.data.enums.DetailOptionsEnum
import es.wokis.projectfinance.databinding.DialogDetailOptionsBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog

class DetailOptionsDialogFragment : BaseBottomSheetDialog() {

    private var binding: DialogDetailOptionsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogDetailOptionsBinding.inflate(layoutInflater, container, false)
        setUpClickListener()
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpClickListener() {
        binding?.apply {
            detailOptionsBtnAddReaction.setOnClickListener {
                returnInfo(DetailOptionsEnum.ADD_REACTION)
            }

            detailOptionsBtnRemoveInvoice.setOnClickListener {
                returnInfo(DetailOptionsEnum.REMOVE_INVOICE)
            }

            detailOptionsBtnEditInvoice.setOnClickListener {
                returnInfo(DetailOptionsEnum.EDIT_INVOICE)
            }
        }
    }

    private fun returnInfo(optionSelected: DetailOptionsEnum) {
        val bundle = Bundle().apply {
            putString(InvoiceDetailDialogFragment.OPTION_SELECTED, optionSelected.key)
        }
        setFragmentResult(RequestKeys.DETAIL_OPTION_SELECTED, bundle)
        navigateBack()
    }

    companion object {
        private const val MAX_ALPHA = 255
    }
}