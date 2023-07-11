package es.wokis.projectfinance.ui.login.fragment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import es.wokis.projectfinance.MobileNavigationDirections
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.data.response.ErrorType
import es.wokis.projectfinance.databinding.DialogChangePassBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.login.viewmodel.ChangePasswordViewModel
import es.wokis.projectfinance.ui.profile.filter.whiteSpaceFilter
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.isTrue
import es.wokis.projectfinance.utils.isValidEmail
import es.wokis.projectfinance.utils.orZero
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.show

class ChangePasswordDialogFragment : BaseBottomSheetDialog() {

    private val args: ChangePasswordDialogFragmentArgs by navArgs()
    private val viewModel: ChangePasswordViewModel by viewModels()

    private var binding: DialogChangePassBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChangePassBinding.inflate(inflater, container, false)
        if (args.recoverMode) {
            setUpRecoverMode()

        } else {
            setUpView()
            initializeListeners()
        }
        setUpCloseBtn()
        return binding?.root
    }

    private fun setUpCloseBtn() {
        binding?.apply {
            changePassBtnClose.setOnClickListener {
                navigateBack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setUpFragmentResultListener()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpFragmentResultListener() {
        setUpChangePassListener()
    }

    private fun setUpChangePassListener() {
        setFragmentResultListener(
            CHANGE_PASS_TOTP_KEY
        ) { _, _ ->
            onAccept()
        }
    }

    private fun setUpRecoverMode() {
        binding?.apply {
            changePassContainerEmail.show()
            changePassInputEmail.filters = arrayOf(whiteSpaceFilter)
            changePassBtnAccept.setOnClickListener {
                onRecoverAccept()
            }
            changePassBtnAccept.text = getString(R.string.change_pass__request_code)
            changePassInputEmail.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                changePassBtnAccept.isEnabled = changePassInputEmail.text?.toString().let {
                    it.isNullOrEmpty().not()
                }.isTrue()
            })
            changePassContainerRecover.hide()
            loginContainerOldPass.hide()
            loginBtnShowOldPass.hide()
            loginContainerNewPass.hide()
            loginBtnShowPass.hide()
        }
    }

    private fun onRecoverAccept() {
        binding?.changePassInputEmail?.apply {
            if (text?.toString().isNullOrEmpty() ||
                text?.toString()?.isValidEmail().isTrue().not()
            ) {
                error = getString(R.string.login__error_invalid_email)
                return
            }

            viewModel.requestRecoverCode(text.toString())
        }

    }

    private fun setUpView() {
        binding?.apply {
            changePassContainerEmail.hide()
            loginContainerOldPass.setVisible(args.recoverMode.not())
            loginBtnShowOldPass.setVisible(args.recoverMode.not())
            changePassContainerRecover.setVisible(args.recoverMode)
            loginContainerNewPass.show()
            loginBtnShowPass.show()

            changePassInputRecover.filters = arrayOf(whiteSpaceFilter)
            loginInputNewPass.filters = arrayOf(whiteSpaceFilter)
            loginInputOldPass.filters = arrayOf(whiteSpaceFilter)

            changePassBtnAccept.text = getString(R.string.change_pass__accept)
        }
    }

    private fun initializeListeners() {
        binding?.apply {
            changePassBtnAccept.setOnClickListener {
                onAccept()
            }

            loginBtnShowPass.setOnClickListener {
                onShowPasswordClicked(loginInputNewPass, loginBtnShowPass)
            }

            loginBtnShowOldPass.setOnClickListener {
                onShowPasswordClicked(loginInputOldPass, loginBtnShowOldPass)
            }

            onTextChangedListener(changePassInputRecover)
            onTextChangedListener(loginInputOldPass)
            onTextChangedListener(loginInputNewPass)
        }
    }

    private fun onShowPasswordClicked(input: EditText, showPassButton: ImageView) {
        input.apply {
            val isPasswordNotVisible =
                inputType != (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            showPassButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, if (isPasswordNotVisible) {
                        R.drawable.ic_visibility_off

                    } else {
                        R.drawable.ic_visibility
                    },
                    context.theme
                )

            )
            inputType = if (isPasswordNotVisible) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            input.setSelection(input.text.length)
        }
    }

    private fun onTextChangedListener(editText: EditText) {
        editText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            // if it is recover mode, code cannot be empty
            // if it is not, old pass cannot be empty
            val firstCondition = if (args.recoverMode) {
                getRecoverCode().isNullOrBlank().not()

            } else {
                getOldPass().isNullOrBlank().not()
            }
            binding?.changePassBtnAccept?.isEnabled = firstCondition &&
                    getNewPass().isNotBlank()
        })
    }

    private fun onAccept() {
        binding?.apply {
            if (args.recoverMode &&
                getRecoverCode().isNullOrEmpty()
            ) {
                changePassInputRecover.error = getString(R.string.change_pass__error_recover_code)
                return
            }

            if (args.recoverMode.not() &&
                getOldPass()?.length.orZero() < 8
            ) {
                loginInputOldPass.error = getString(R.string.login__error_invalid_password)
                return
            }

            if (getNewPass().length < 8) {
                loginInputOldPass.error = getString(R.string.login__error_invalid_password)
                return
            }

            viewModel.changePassword(
                newPass = getNewPass(),
                oldPass = getOldPass(),
                recoverCode = getRecoverCode(),
                recoverMode = args.recoverMode
            )
        }
    }

    private fun setUpObservers() {
        setUpRequestRecoverCodeObserver()
        setUpRecoverPassObserver()
        setUpChangePassObserver()
    }

    private fun setUpChangePassObserver() {
        viewModel.getChangeUserPassLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    hideLoading()
                    when (it.error) {
                        is ErrorType.TOTPRequiredError -> {
                            navigateTo(
                                ChangePasswordDialogFragmentDirections.actionChangePassToTotp(
                                    CHANGE_PASS_TOTP_KEY
                                )
                            )
                        }

                        is ErrorType.ServerError -> {
                            when (it.error.httpCode) {
                                409 -> {
                                    showSnackBarMessage(
                                        R.string.change_pass__wrong_pass,
                                        SNACK_BAR_DURATION
                                    )
                                    navigateBack()
                                }

                                else -> showChangePassError()
                            }
                        }

                        else -> {
                            showChangePassError()
                        }
                    }
                }

                is AsyncResult.Loading -> {
                    showLoading(R.string.change_pass__changing_pass)
                }

                is AsyncResult.Success -> {
                    hideLoading()
                    binding?.apply {
                        showSnackBarMessage(
                            R.string.change_pass__changed_pass_success,
                            SNACK_BAR_DURATION
                        )
                        navigateTo(MobileNavigationDirections.actionGlobalToHome())
                    }
                }
            }
        }
    }

    private fun showChangePassError() {
        showSnackBarMessage(
            R.string.change_pass__error_change_pass,
            SNACK_BAR_DURATION
        )
        navigateBack()
    }

    private fun setUpRecoverPassObserver() {
        viewModel.getRecoverUserPassLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    hideLoading()
                    when (it.error) {
                        is ErrorType.ServerError -> {
                            when (it.error.httpCode) {
                                409 -> {
                                    showSnackBarMessage(
                                        R.string.change_pass__wrong_pass,
                                        SNACK_BAR_DURATION
                                    )
                                    navigateBack()
                                }

                                400 -> showSnackBarMessage(
                                    R.string.change_pass__unknown_recover_code,
                                    SNACK_BAR_DURATION
                                )

                                else -> showChangePassError()
                            }
                        }

                        else -> showChangePassError()
                    }
                }

                is AsyncResult.Loading -> {
                    showLoading(R.string.change_pass__changing_pass)
                }

                is AsyncResult.Success -> {
                    hideLoading()
                    showSnackBarMessage(
                        R.string.change_pass__changed_pass_success,
                        duration = SNACK_BAR_DURATION
                    )
                    navigateBack()
                }
            }
        }
    }

    private fun setUpRequestRecoverCodeObserver() {
        viewModel.getRequestRecoverCodeLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    hideLoading()
                    binding?.apply {
                        showSnackBarMessage(
                            R.string.change_pass__error_requesting_recover,
                            duration = SNACK_BAR_DURATION
                        )
                        navigateBack()
                    }
                }

                is AsyncResult.Loading -> {
                    showLoading(R.string.change_pass__request_recover_pass)
                }

                is AsyncResult.Success -> {
                    hideLoading()
                    setUpView()
                    initializeListeners()
                }
            }
        }
    }

    private fun showLoading(@StringRes text: Int) {
        binding?.apply {
            changePassIncludeLoading.root.show()
            changePassIncludeLoading.loadingLabelLoadingText.text =
                getString(text)
        }
    }

    private fun hideLoading() {
        binding?.changePassIncludeLoading?.root.hide()
    }

    private fun getRecoverCode() = binding?.changePassInputRecover?.text?.toString()

    private fun getOldPass() = binding?.loginInputOldPass?.text?.toString()

    private fun getNewPass() = binding?.loginInputNewPass?.text?.toString().orEmpty()

    companion object {
        private const val SNACK_BAR_DURATION = 1500
        const val CHANGE_PASS_TOTP_KEY = "CHANGE_PASS_TOTP_KEY"
    }

}