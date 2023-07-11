package es.wokis.projectfinance.ui.addinvoice.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.DialogCloudSynchronizationBinding
import es.wokis.projectfinance.ui.addinvoice.fragment.AddInvoiceDialogFragment.Companion.SHOULD_SYNC
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog

class CloudSynchronizationDialogFragment : BaseBottomSheetDialog() {
    private val args: CloudSynchronizationDialogFragmentArgs by navArgs()

    private var binding: DialogCloudSynchronizationBinding? = null
    private var shouldSync: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCloudSynchronizationBinding.inflate(inflater, container, false)
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
        val sync = args.synchronize
        updateSync(sync)
    }

    private fun setUpListeners() {
        binding?.apply {
            cloudSyncTypeContainerSyncOn.setOnClickListener {
                updateSync(true)
            }

            cloudSyncTypeContainerSyncOff.setOnClickListener {
                updateSync(false)
            }

            cloudSyncTypeBtnAccept.setOnClickListener {
                onAccept()
            }

            cloudSyncTypeBtnClose.setOnClickListener {
                navigateBack()
            }
        }
    }

    private fun onAccept() {
        val bundle = Bundle().apply {
            putBoolean(SHOULD_SYNC, shouldSync)
        }
        setFragmentResult(RequestKeys.ADD_INVOICE_TO_SYNC, bundle)
        navigateBack()
    }

    private fun updateSync(sync: Boolean) {
        shouldSync = sync
        binding?.apply {
            cloudSyncTypeRadioSyncOn.isChecked = sync
            cloudSyncTypeRadioSyncOff.isChecked = !sync
        }
    }
}