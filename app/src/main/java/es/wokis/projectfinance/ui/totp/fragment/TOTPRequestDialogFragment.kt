package es.wokis.projectfinance.ui.totp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import es.wokis.projectfinance.databinding.DialogTotpRequestBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.totp.viewmodel.TOTPViewModel
import es.wokis.projectfinance.utils.orZero

class TOTPRequestDialogFragment : BaseBottomSheetDialog() {

    private val args: TOTPRequestDialogFragmentArgs by navArgs()
    private val viewModel: TOTPViewModel by viewModels()

    private var binding: DialogTotpRequestBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogTotpRequestBinding.inflate(layoutInflater, container, false)
        setUpListeners()
        return binding?.root
    }

    private fun setUpListeners() {
        binding?.apply {
            totpRequestBtnAccept.setOnClickListener {
                returnInfo()
            }

            totpRequestInputCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // no-op
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    totpRequestBtnAccept.isEnabled = p0?.length.orZero() == 6
                }

                override fun afterTextChanged(p0: Editable?) {
                    // no-op
                }

            })

            totpRequestBtnClose.setOnClickListener {
                navigateBack()
            }
        }
    }

    private fun getTotpCode() = binding?.totpRequestInputCode?.text?.toString()

    private fun returnInfo() {
        viewModel.saveTotpCode(getTotpCode())
        setFragmentResult(
            args.requestKey,
            bundleOf()
        )
        navigateBack()
    }

}