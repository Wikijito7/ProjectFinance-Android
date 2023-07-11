package es.wokis.projectfinance.ui.totp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.totp.TOTPResponseBO
import es.wokis.projectfinance.data.domain.totp.ActivateTotpUseCase
import es.wokis.projectfinance.data.domain.totp.SaveTotpCodeUseCase
import es.wokis.projectfinance.data.domain.totp.SetTotpEnabledUseCase
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TOTPViewModel @Inject constructor(
    private val saveTotpCodeUseCase: SaveTotpCodeUseCase,
    private val activateTotpUseCase: ActivateTotpUseCase,
    private val setTotpEnabledUseCase: SetTotpEnabledUseCase
) : ViewModel() {

    private val saveTotpLiveData: MutableLiveData<AsyncResult<TOTPResponseBO>> = MutableLiveData()

    fun getSaveTotpLiveData() = saveTotpLiveData as LiveData<AsyncResult<TOTPResponseBO>>

    fun saveTotpCode(code: String?) {
        code?.let {
            saveTotpCodeUseCase(it)
        }
    }

    fun requestTOTP() {
        viewModelScope.launch(Dispatchers.IO) {
            activateTotpUseCase().collect {
                saveTotpLiveData.postValue(it)
            }
        }
    }

    fun setTotpEnabled() {
        setTotpEnabledUseCase(true)
    }

}