package es.wokis.projectfinance.ui.login.fragment

import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import dagger.hilt.android.AndroidEntryPoint
import es.wokis.projectfinance.MobileNavigationDirections
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.domain.user.UserAlreadyExistsError
import es.wokis.projectfinance.data.domain.user.WrongUsernameOrPasswordError
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.data.response.ErrorType
import es.wokis.projectfinance.databinding.FragmentLoginBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.login.viewmodel.LoginViewModel
import es.wokis.projectfinance.ui.profile.filter.whiteSpaceFilter
import es.wokis.projectfinance.utils.applyEdgeToEdge
import es.wokis.projectfinance.utils.getLocale
import es.wokis.projectfinance.utils.hideKeyboard
import es.wokis.projectfinance.utils.isValidEmail
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.showSnackBar


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModels()

    private var binding: FragmentLoginBinding? = null

    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setUpLoginWithGoogle()
        setUpListeners()
        setUpInputs()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLoginMode = true
        setUpView()
        setUpObservers()
        setUpFragmentResultListeners()
    }

    private fun setUpFragmentResultListeners() {
        setUpLoginListener()
        setUpLoginWithGoogleListener()
    }

    private fun setUpLoginListener() {
        setFragmentResultListener(
            LOGIN_TOTP_KEY
        ) { _, _ ->
            viewModel.retryLogin()
        }
    }

    private fun setUpLoginWithGoogleListener() {
        setFragmentResultListener(
            LOGIN_WITH_GOOGLE_TOTP_KEY
        ) { _, _ ->
            viewModel.retryLoginWithGoogle()
        }
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
        hideBottomNavigation()
        hideAddInvoiceButton()
        interceptBackPressed {
            navigateTo(MobileNavigationDirections.actionGlobalToHome())
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun showInvoiceRemovedSnackBar() {
        // no-op
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient?.getSignInCredentialFromIntent(data)
                    val idToken = credential?.googleIdToken
                    val username = credential?.id
                    val password = credential?.password
                    when {
                        idToken != null -> {
                            viewModel.loginWithGoogle(idToken)
                        }

                        password != null -> {
                            username?.let {
                                context?.getLocale()?.language?.let { lang ->
                                    viewModel.continueWithLoginOrRegister(
                                        it,
                                        password,
                                        lang = lang.lowercase()
                                    )
                                }
                            }
                        }

                        else -> {
                            setLoading(false)
                            showLoginWithGoogleErrorSnackBar()
                        }
                    }

                } catch (e: ApiException) {
                    setLoading(false)
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(ContentValues.TAG, "One-tap dialog was closed.")
                        }

                        else -> showLoginWithGoogleErrorSnackBar()
                    }
                }
            }
        }
    }

    private fun getEmail() = binding?.loginInputEmail?.text?.toString().orEmpty()

    private fun getUsername() = binding?.loginInputUsername?.text?.toString().orEmpty()

    private fun getPassword() = binding?.loginInputPassword?.text?.toString().orEmpty()

    private fun setUpObservers() {
        viewModel.getAuthLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AsyncResult.Error -> {
                    setLoading(false)
                    when (it.error) {
                        is ErrorType.TOTPRequiredError -> showTotpDialog()
                        else -> showSnackbarError(it)
                    }

                }

                is AsyncResult.Loading -> {
                    setLoading(
                        true,
                        getString(
                            if (viewModel.isLoginMode) {
                                R.string.login__loading_sign_in

                            } else {
                                R.string.login__loading_sign_up
                            }
                        )
                    )
                }

                is AsyncResult.Success -> {
                    setLoading(false)
                    if (shouldAskForNotificationPermissions()) {
                        navigateTo(LoginFragmentDirections.actionLoginToNotificationRationale())

                    } else {
                        navigateTo(LoginFragmentDirections.actionLoginToProfile())
                    }
                    viewModel.clearOtherUserData()
                    launchSynchronizeWorker()
                }
            }
        }
    }

    private fun showTotpDialog() {
        navigateTo(
            LoginFragmentDirections.actionLoginToTotp(
                if (viewModel.isLoginWithGoogle()) {
                    LOGIN_WITH_GOOGLE_TOTP_KEY

                } else {
                    LOGIN_TOTP_KEY
                }
            )
        )
    }

    private fun showSnackbarError(it: AsyncResult.Error<Boolean>) {
        binding?.root?.let { view ->
            showSnackBar(
                when (it.error) {
                    is WrongUsernameOrPasswordError -> R.string.login__error_wrong_username_or_password
                    is UserAlreadyExistsError -> R.string.login__error_email_or_username_exists
                    is ErrorType.ServerError -> R.string.general__server_error
                    else -> R.string.login__error_while_login
                },
                R.string.general__retry,
                view, action = {
                    onClickAccept()
                }
            )
        }
    }

    private fun setUpListeners() {
        binding?.apply {
            loginBtnClose.setOnClickListener {
                navigateTo(MobileNavigationDirections.actionGlobalToHome())
            }

            loginBtnHelp.setOnClickListener {
                navigateTo(LoginFragmentDirections.actionLoginToHelp())
            }

            loginBtnResetPassword.setOnClickListener {
                navigateTo(LoginFragmentDirections.actionLoginToRecoverPass(true))
            }

            loginBtnGoogle.setOnClickListener {
                showLoginWithGoogleDialog()
                setLoading(true, getString(R.string.login__loading_sign_in))
            }

            loginBtnAccept.setOnClickListener {
                onClickAccept()
            }

            loginBtnAccount.setOnClickListener {
                viewModel.isLoginMode = !viewModel.isLoginMode
                setUpView()
            }

            loginBtnShowPass.setOnClickListener {
                onShowPasswordClicked()
            }

            onTextChanged(loginInputEmail)
            onTextChanged(loginInputUsername)
            onTextChanged(loginInputPassword)
        }
    }

    private fun onClickAccept() {
        binding?.apply {
            if (viewModel.isLoginMode.not()) {
                if (!getEmail().isValidEmail()) {
                    loginInputEmail.error = getString(R.string.login__error_invalid_email)
                    return
                }

                if (getUsername().length < 3) {
                    loginInputUsername.error = getString(R.string.login__error_invalid_username)
                    return
                }

                if (getPassword().length < 8) {
                    loginInputPassword.error = getString(R.string.login__error_invalid_password)
                    return
                }

            }
            val lang = context?.getLocale()
            lang?.language?.let { langName ->
                viewModel.continueWithLoginOrRegister(
                    getUsername(),
                    getPassword(),
                    lang = langName.lowercase(),
                    getEmail()
                )
            }
            hideKeyboard(root)
        }
    }

    private fun onTextChanged(v: EditText) {
        v.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no-op
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateAcceptBtn()
            }

            override fun afterTextChanged(p0: Editable?) {
                // no-op
            }

        })
    }

    private fun updateAcceptBtn() {
        val emailAndPasswordNotEmpty = getUsername().isNotBlank() &&
                getPassword().isNotBlank()
        val registerModeAndEmailNotEmpty =
            getEmail().takeIf { !viewModel.isLoginMode }?.isNotBlank() ?: true

        binding?.loginBtnAccept?.isEnabled = emailAndPasswordNotEmpty &&
                registerModeAndEmailNotEmpty
    }

    private fun setUpInputs() {
        binding?.apply {
            loginInputEmail.filters = arrayOf(whiteSpaceFilter)
            loginInputPassword.filters = arrayOf(whiteSpaceFilter)
            loginInputUsername.filters = arrayOf(whiteSpaceFilter)
        }
    }

    private fun FragmentLoginBinding.onShowPasswordClicked() {
        loginInputPassword.apply {
            val isPasswordNotVisible =
                inputType != (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            loginBtnShowPass.setImageDrawable(
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
            loginInputPassword.setSelection(getPassword().length)
        }
    }

    private fun setUpView() {
        viewModel.isLoginMode.let { loginMode ->
            binding?.apply {
                loginContainerEmail.setVisible(!loginMode)
                loginBtnResetPassword.setVisible(loginMode)
                loginContainerUsername.hint = context?.getString(
                    if (loginMode) {
                        R.string.login__username_or_email

                    } else {
                        R.string.login__username
                    }
                )
                loginBtnAccount.text = context?.getString(
                    if (loginMode) {
                        R.string.login__account_sign_up

                    } else {
                        R.string.login__account_sign_in
                    }
                )
                loginLabelTitle.text = context?.getString(
                    if (loginMode) {
                        R.string.login__title_sign_in

                    } else {
                        R.string.login__title_sign_up
                    }
                )
                loginBtnAccept.text = context?.getString(
                    if (loginMode) {
                        R.string.login__sign_in

                    } else {
                        R.string.login__sign_up
                    }
                )
                root.applyEdgeToEdge(
                    applyTopPadding = true,
                    applyBottomPadding = true,
                    applyLeftPadding = true,
                    applyRightPadding = true
                )
            }
        }
        updateAcceptBtn()
    }

    private fun setUpLoginWithGoogle() {
        activity?.let {
            oneTapClient = Identity.getSignInClient(it)
            signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(
                    BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build()
                )
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(true)
                .build()
        }
    }

    private fun showLoginWithGoogleDialog() {
        activity?.let {
            signInRequest?.let { request ->
                oneTapClient?.beginSignIn(request)?.addOnSuccessListener(it) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender,
                            REQ_ONE_TAP,
                            null,
                            0,
                            0,
                            0,
                            null
                        )

                    } catch (e: IntentSender.SendIntentException) {
                        setLoading(false)
                        showLoginWithGoogleErrorSnackBar()
                    }

                }?.addOnFailureListener(it) {
                    setLoading(false)
                    showLoginWithGoogleErrorSnackBar()
                }
            }
        }
    }

    private fun showLoginWithGoogleErrorSnackBar() {
        binding?.loginBtnGoogle?.let {
            showSnackBar(
                R.string.login__error_with_google,
                R.string.general__retry,
                it,
                action = {
                    showLoginWithGoogleDialog()
                }
            )
        }
    }

    companion object {
        private const val REQ_ONE_TAP = 6660777
        private const val LOGIN_TOTP_KEY = "LOGIN_TOTP_KEY"
        private const val LOGIN_WITH_GOOGLE_TOTP_KEY = "LOGIN_WITH_GOOGLE_TOTP_KEY"
    }
}