package es.wokis.projectfinance.ui.login.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.wokis.projectfinance.databinding.DialogLoginHelpBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.utils.underLineText


class LoginHelpDialogFragment : BaseBottomSheetDialog() {

    private var binding: DialogLoginHelpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogLoginHelpBinding.inflate(inflater, container, false)
        setUpDialog()
        setUpView()
        setUpListener()
        setUpDialog()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forceExtendView()
    }

    private fun setUpView() {
        binding?.apply {
            loginHelpTerms.underLineText()
        }
    }


    private fun setUpListener() {
        binding?.apply {
            loginHelpBtnClose.setOnClickListener {
                navigateBack()
            }

            loginHelpTerms.setOnClickListener {
                openTermsWeb()
            }
        }
    }

    private fun openTermsWeb() {
        val url = "https://wokis.es/project-finance/"
        val webIntent = Intent(Intent.ACTION_VIEW)
        webIntent.data = Uri.parse(url)
        startActivity(webIntent)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}