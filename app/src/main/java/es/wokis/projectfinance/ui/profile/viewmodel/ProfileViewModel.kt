package es.wokis.projectfinance.ui.profile.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.user.UpdateUserBO
import es.wokis.projectfinance.data.bo.user.UserBO
import es.wokis.projectfinance.data.domain.invoice.GetNumberOfInvoiceUseCase
import es.wokis.projectfinance.data.domain.totp.SetTotpEnabledUseCase
import es.wokis.projectfinance.data.domain.user.GetBadgesUseCase
import es.wokis.projectfinance.data.domain.user.GetResendTimestampUseCase
import es.wokis.projectfinance.data.domain.user.GetUserUseCase
import es.wokis.projectfinance.data.domain.user.RequestVerificationEmailUseCase
import es.wokis.projectfinance.data.domain.user.SetResendTimestampUseCase
import es.wokis.projectfinance.data.domain.user.SignOutUseCase
import es.wokis.projectfinance.data.domain.user.UpdateUserUseCase
import es.wokis.projectfinance.data.domain.user.UploadImageUseCase
import es.wokis.projectfinance.data.domain.user.UserLoggedInUseCase
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.data.response.ErrorType
import es.wokis.projectfinance.utils.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getNumberOfInvoiceUseCase: GetNumberOfInvoiceUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val userLoggedInUseCase: UserLoggedInUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val setTOTPEnabledUseCase: SetTotpEnabledUseCase,
    private val getBadgesUseCase: GetBadgesUseCase,
    private val getResendTimestampUseCase: GetResendTimestampUseCase,
    private val setResendTimestampUseCase: SetResendTimestampUseCase,
    private val requestVerificationEmailUseCase: RequestVerificationEmailUseCase
) : ViewModel() {

    private var imageUri: Uri? = null
    var username: String? = null
        private set

    private val numberOfInvoicesLiveData: MutableLiveData<Int> = MutableLiveData()
    private val userInfoLiveData: MutableLiveData<AsyncResult<UserBO>> = MutableLiveData()
    private val uploadImageLiveData: MutableLiveData<AsyncResult<Boolean>> = MutableLiveData()
    private val updateUserLiveData: MutableLiveData<AsyncResult<Boolean>> = MutableLiveData()
    private val requestVerificationEmailLiveData: MutableLiveData<AsyncResult<Boolean>> = MutableLiveData()

    fun getNumberOfInvoicesLiveData() = numberOfInvoicesLiveData as LiveData<Int>
    fun getUserInfoLiveData() = userInfoLiveData as LiveData<AsyncResult<UserBO>>
    fun getUploadImageLiveData() = uploadImageLiveData as LiveData<AsyncResult<Boolean>>
    fun getUpdateUserLiveData() = updateUserLiveData as LiveData<AsyncResult<Boolean>>
    fun getRequestVerificationEmailLiveData() = requestVerificationEmailLiveData as LiveData<AsyncResult<Boolean>>

    fun getUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getUserUseCase()
            user.collect {
                if (it is AsyncResult.Success) {
                    username = it.data?.username
                    getNumberOfInvoices()
                    setTOTPEnabledUseCase(it.data?.totpEnabled.isTrue())
                }
                userInfoLiveData.postValue(it)
            }
        }
    }

    private fun getNumberOfInvoices() {
        viewModelScope.launch(Dispatchers.IO) {
            getNumberOfInvoiceUseCase().collect {
                numberOfInvoicesLiveData.postValue(it)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            signOutUseCase()
        }
    }

    fun isUserLoggedIn(): Boolean = userLoggedInUseCase()

    fun uploadImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            imageUri = uri
            uploadImageUseCase(uri).collect {
                if (it !is AsyncResult.Loading) {
                    if (it is AsyncResult.Error &&
                        it.error !is ErrorType.TOTPRequiredError) {
                        deleteFile(uri)
                    }
                }
                uploadImageLiveData.postValue(it)
            }

        }
    }

    fun retryUploadImage() {
        imageUri?.let {
            uploadImage(it)
        }
    }

    private fun deleteFile(uri: Uri) {
        try {
            uri.path?.let {
                File(it).delete()
                imageUri = null

            } ?: throw IllegalStateException("File path is null")

        } catch (exc: Exception) {
            Log.d(
                "IMAGE",
                "uploadImage: Error while deleting file: ${exc.stackTraceToString()}"
            )
        }
    }

    fun updateUserInfo(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserUseCase(
                UpdateUserBO(username)
            ).collect {
                updateUserLiveData.postValue(it)
            }
        }
    }

    fun getBadges() = getBadgesUseCase()

    fun getResendTimestamp(): Long = getResendTimestampUseCase()

    fun canResendEmail() = System.currentTimeMillis() > getResendTimestamp()

    fun setResendTimestamp() {
        setResendTimestampUseCase(System.currentTimeMillis() + RESEND_COOL_DOWN)
    }

    fun requestVerificationEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            requestVerificationEmailUseCase().collect {
                requestVerificationEmailLiveData.postValue(it)
            }
        }
    }

    companion object {
        private const val RESEND_COOL_DOWN: Long = 15 * 60 * 1000 // 15 minutes
    }

}