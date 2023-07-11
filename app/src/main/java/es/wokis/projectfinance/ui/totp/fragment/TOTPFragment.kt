package es.wokis.projectfinance.ui.totp.fragment

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.flexbox.FlexDirection.ROW
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.totp.TOTPResponseBO
import es.wokis.projectfinance.data.constants.AppConstants.NEW_LINE
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.databinding.FragmentTotpBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.totp.adapter.RecoverWordsAdapter
import es.wokis.projectfinance.ui.totp.viewmodel.TOTPViewModel
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.show
import es.wokis.projectfinance.utils.showSnackBar


class TOTPFragment : BaseFragment() {

    private val viewModel: TOTPViewModel by viewModels()

    private var binding: FragmentTotpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTotpBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        viewModel.requestTOTP()
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
        hideAddInvoiceButton()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpObservers() {
        setUpSaveTotpObserver()
    }

    private fun setUpSaveTotpObserver() {
        viewModel.getSaveTotpLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    setLoading(false)
                    binding?.apply {
                        showSnackBar(R.string.totp__error_setting_up, view = totpBtnCopyTotp)
                        navigateBack()
                    }
                }

                is AsyncResult.Loading -> setLoading(
                    true,
                    getString(R.string.totp__requesting_totp)
                )

                is AsyncResult.Success -> {
                    setLoading(false)
                    viewModel.setTotpEnabled()
                    it.data?.let { totp ->
                        setUpView(totp)
                    }
                }
            }
        }
    }

    private fun setUpView(totp: TOTPResponseBO) {
        binding?.apply {
            initializeListeners(totp)
            totpLabelCode.text = totp.encodedSecret
            setUpQrImage(totp.totpUrl)
        }
    }

    private fun FragmentTotpBinding.setUpQrImage(totpUrl: String) {
        val writer = MultiFormatWriter()
        try {
            val matrix = writer.encode(totpUrl, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT)
            val encoder = BarcodeEncoder()
            val bitmap = encoder.createBitmap(matrix)
            totpImgScanQr.setImageBitmap(bitmap)

        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun initializeListeners(totp: TOTPResponseBO) {
        binding?.apply {
            totpBtnLaunchTotp.setOnClickListener {
                val url = totp.totpUrl
                val totpIntent = Intent(Intent.ACTION_VIEW)
                totpIntent.data = Uri.parse(url)
                try {
                    startActivity(totpIntent)

                } catch (e: ActivityNotFoundException) {
                    showSnackBar(R.string.totp__no_app_found, view = totpBtnCopyTotp)
                }
            }

            totpBtnCopyTotp.setOnClickListener {
                context?.let {
                    val clipboard: ClipboardManager? =
                        it.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
                    val clip = ClipData.newPlainText(
                        getString(R.string.totp__qr_label),
                        totp.encodedSecret
                    )
                    clipboard?.setPrimaryClip(clip)
                    Toast.makeText(it, R.string.totp__text_copied, Toast.LENGTH_SHORT).show()
                }
            }

            totpBtnGoNext.setOnClickListener {
                showRecoverWords(totp)
            }
        }
    }

    private fun FragmentTotpBinding.showRecoverWords(totp: TOTPResponseBO) {
        totpContainerQr.hide()
        totpContainerRecoverWords.show()
        totpListWords.layoutManager = FlexboxLayoutManager(context, ROW)
        totpListWords.adapter = RecoverWordsAdapter().apply {
            submitList(totp.words)
        }

        totpBtnCopyWords.setOnClickListener {
            context?.let {
                val clipboard: ClipboardManager? =
                    it.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
                val clip = ClipData.newPlainText(
                    getString(R.string.totp__words_label),
                    totp.words.joinToString(separator = NEW_LINE)
                )
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(it, R.string.totp__text_copied, Toast.LENGTH_SHORT).show()
            }
        }

        totpBtnGoNext.text = getText(R.string.totp__finish)
        totpBtnGoNext.setOnClickListener {
            setFragmentResult(
                RequestKeys.CONFIG_TO_TOTP,
                bundleOf()
            )
            navigateBack()
        }
    }

    companion object {
        private const val QR_WIDTH = 400
        private const val QR_HEIGHT = 400
    }
}