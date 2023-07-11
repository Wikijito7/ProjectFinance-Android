package es.wokis.projectfinance.ui.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.user.AuthUserBO
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.data.domain.clear.ClearOtherUserDataUseCase
import es.wokis.projectfinance.data.domain.user.GetUserUseCase
import es.wokis.projectfinance.data.domain.user.LoginUseCase
import es.wokis.projectfinance.data.domain.user.LoginWithGoogleUseCase
import es.wokis.projectfinance.data.domain.user.RegisterUseCase
import es.wokis.projectfinance.data.domain.user.SaveCurrentUserIdUseCase
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.utils.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val saveCurrentUserIdUseCase: SaveCurrentUserIdUseCase,
    private val clearOtherUserDataUseCase: ClearOtherUserDataUseCase
) : ViewModel() {

    var isLoginMode: Boolean = false

    private var authLiveData: MutableLiveData<AsyncResult<Boolean>> = MutableLiveData()

    private var authUser: AuthUserBO? = null
    private var authToken: String = EMPTY_TEXT
    private var loginWithGoogle = false

    fun getAuthLiveData() = authLiveData as LiveData<AsyncResult<Boolean>>

    fun continueWithLoginOrRegister(
        username: String,
        password: String,
        lang: String,
        email: String = EMPTY_TEXT
    ) {
        authUser = AuthUserBO(username, password, email.takeIf { it.isNotBlank() }, lang)
        loginWithGoogle = false
        doLoginOrRegister(authUser)
    }

    private fun doLoginOrRegister(user: AuthUserBO?) {
        viewModelScope.launch(Dispatchers.IO) {
            user?.let {
                if (isLoginMode) {
                    loginUseCase(user)

                } else {
                    registerUseCase(user)
                }.collect {
                    synchronizeUserId(it)
                    authLiveData.postValue(it)
                }
            }
        }
    }

    private suspend fun synchronizeUserId(it: AsyncResult<Boolean>) {
        if (it is AsyncResult.Success) {
            getUserUseCase().collect { userAsyncResult ->
                if (userAsyncResult is AsyncResult.Success) {
                    userAsyncResult.data?.id?.let { id ->
                        saveCurrentUserIdUseCase(id)
                    }.isTrue()
                }
            }
        }
    }

    fun loginWithGoogle(authToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            this@LoginViewModel.authToken = authToken
            this@LoginViewModel.loginWithGoogle = true
            loginWithGoogleUseCase(authToken).collect {
                synchronizeUserId(it)
                authLiveData.postValue(it)
            }
        }
    }

    fun clearOtherUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            clearOtherUserDataUseCase()
        }
    }

    fun retryLogin() {
        doLoginOrRegister(authUser)
    }

    fun retryLoginWithGoogle() {
        loginWithGoogle(authToken)
    }

    fun isLoginWithGoogle(): Boolean = loginWithGoogle

}