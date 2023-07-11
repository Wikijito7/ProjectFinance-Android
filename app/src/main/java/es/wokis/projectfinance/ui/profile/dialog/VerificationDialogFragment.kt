package es.wokis.projectfinance.ui.profile.dialog

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.databinding.DialogVerificationBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.profile.viewmodel.ProfileViewModel
import es.wokis.projectfinance.utils.getPrimaryColor
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.show
import es.wokis.projectfinance.utils.showSnackBar


class VerificationDialogFragment : BaseBottomSheetDialog() {

    private val viewModel: ProfileViewModel by viewModels()

    private var binding: DialogVerificationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogVerificationBinding.inflate(inflater, container, false)
        setUpDialog()
        setUpView()
        setUpListeners()
        return binding?.root
    }

    private fun setUpView() {
        binding?.apply {
            verificationLabelResend.setUpButton()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpObservers() {
        setUpSendVerificationObserver()
    }

    private fun setUpSendVerificationObserver() {
        viewModel.getRequestVerificationEmailLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    hideLoading()
                    onRequestEmail(R.string.verification__sent_error)
                }

                is AsyncResult.Loading -> {
                    showLoading(R.string.verification__request)
                }

                is AsyncResult.Success -> {
                    hideLoading()
                    onRequestEmail(R.string.verification__sent_success)
                }
            }
        }
    }

    private fun onRequestEmail(@StringRes title: Int) {
        viewModel.setResendTimestamp()
        binding?.verificationLabelResend?.apply {
            setUpButton()
            showSnackBar(title, view = this)
        }
    }

    private fun showLoading(@StringRes text: Int) {
        binding?.apply {
            verificationIncludeLoading.root.show()
            verificationIncludeLoading.loadingLabelLoadingText.text =
                getString(text)
        }
    }

    private fun hideLoading() {
        binding?.verificationIncludeLoading?.root.hide()
    }

    private fun setUpListeners() {
        binding?.apply {
            verificationBtnClose.setOnClickListener {
                navigateBack()
            }

            verificationLabelResend.setOnClickListener {
                tryResendVerificationMail()
            }
        }
    }

    private fun tryResendVerificationMail() {
        if (viewModel.canResendEmail()) {
            viewModel.requestVerificationEmail()
        }
    }

    private fun TextView.setUpButton() {
        var time = viewModel.getResendTimestamp() - System.currentTimeMillis()
        if (viewModel.canResendEmail()) {
            text = getString(R.string.verification__resend)
            context.theme.getPrimaryColor()?.let {
                setTextColor(it)
            }

        } else {
            setTextColor(ResourcesCompat.getColor(resources, R.color.light_gray, context.theme))
            object : CountDownTimer(time, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    text = getString(R.string.verification__resend_in, getTimeFormatted(millisUntilFinished))
                    time--
                }

                override fun onFinish() {
                    setUpButton()
                }

                private fun getTimeFormatted(millisUntilFinished: Long): String {
                    val totalSecs = millisUntilFinished / 1000
                    val minutes = (totalSecs % 3600) / 60
                    val seconds = totalSecs % 60

                    return String.format("%02d:%02d", minutes, seconds);
                }
            }.start()
        }
    }
}
