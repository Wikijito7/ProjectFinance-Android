package es.wokis.projectfinance.ui.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.domain.user.ChangeUserPassUseCase
import es.wokis.projectfinance.data.domain.user.CloseAllSessionsUseCase
import es.wokis.projectfinance.data.domain.user.RecoverUserPassUseCase
import es.wokis.projectfinance.data.domain.user.RequestRecoverPassUseCase
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val closeAllSessionsUseCase: CloseAllSessionsUseCase,
    private val changeUserPassUseCase: ChangeUserPassUseCase,
    private val recoverUserPassUseCase: RecoverUserPassUseCase,
    private val requestRecoverPassUseCase: RequestRecoverPassUseCase
) : ViewModel() {

    private val requestRecoverCodeLiveData: MutableLiveData<AsyncResult<Boolean>> = MutableLiveData()
    private val changeUserPassLiveData: MutableLiveData<AsyncResult<Boolean>> = MutableLiveData()
    private val recoverUserPassLiveData: MutableLiveData<AsyncResult<Boolean>> = MutableLiveData()
    private val closeAllSessionsLiveData: MutableLiveData<AsyncResult<Boolean>> = MutableLiveData()

    fun getRequestRecoverCodeLiveData() = requestRecoverCodeLiveData as LiveData<AsyncResult<Boolean>>
    fun getChangeUserPassLiveData() = changeUserPassLiveData as LiveData<AsyncResult<Boolean>>
    fun getRecoverUserPassLiveData() = recoverUserPassLiveData as LiveData<AsyncResult<Boolean>>
    fun getCloseAllSessionsLiveData() = closeAllSessionsLiveData as LiveData<AsyncResult<Boolean>>

    fun requestRecoverCode(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            requestRecoverPassUseCase(email).collect {
                requestRecoverCodeLiveData.postValue(it)
            }
        }
    }

    fun changePassword(
        newPass: String,
        oldPass: String? = null,
        recoverCode: String? = null,
        recoverMode: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (recoverMode) {
                recoverUserPassUseCase(recoverCode.orEmpty(), newPass).collect {
                    recoverUserPassLiveData.postValue(it)
                }

            } else {

                changeUserPassUseCase(oldPass.orEmpty(), newPass).collect {
                    changeUserPassLiveData.postValue(it)
                }
            }
        }
    }

    fun closeAllSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            closeAllSessionsUseCase().collect {
                closeAllSessionsLiveData.postValue(it)
            }
        }
    }
}