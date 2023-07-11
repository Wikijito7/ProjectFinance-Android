package es.wokis.projectfinance.ui.profile.viewmodel

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.constants.AppConstants
import es.wokis.projectfinance.data.domain.totp.RemoveTotpUseCase
import es.wokis.projectfinance.data.domain.user.CloseAllSessionsUseCase
import es.wokis.projectfinance.data.enums.SelectedThemeEnum
import es.wokis.projectfinance.data.event.Event
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val closeAllSessionsUseCase: CloseAllSessionsUseCase,
    private val removeTotpUseCase: RemoveTotpUseCase
) : ViewModel() {

    private val closeAllSessionsLiveData: MutableLiveData<AsyncResult<Boolean>> = MutableLiveData()
    private val removeTotpLiveData: MutableLiveData<Event<AsyncResult<Boolean>>> = MutableLiveData()

    fun getRemoveTotpLiveData() = removeTotpLiveData as LiveData<Event<AsyncResult<Boolean>>>

    fun getCloseAllSessionsLiveData() = closeAllSessionsLiveData as LiveData<AsyncResult<Boolean>>

    fun shouldActivateBiometrics(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(AppConstants.BIOMETRICS_ENABLED, enabled)
        }
    }

    fun shouldActivateSecureMode(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(AppConstants.SECURE_MODE_ENABLED, enabled)
        }
    }

    fun setSelectedTheme(selectedTheme: SelectedThemeEnum) {
        sharedPreferences.edit {
            putString(AppConstants.SELECTED_THEME, selectedTheme.key)
        }
    }

    fun closeAllSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            closeAllSessionsUseCase().collect {
                closeAllSessionsLiveData.postValue(it)
            }
        }
    }

    fun getSelectedTheme() =
        SelectedThemeEnum.fromKey(sharedPreferences.getString(AppConstants.SELECTED_THEME, null))

    fun isBiometricsEnabled() = sharedPreferences.getBoolean(AppConstants.BIOMETRICS_ENABLED, false)

    fun isTwoFactorAuthEnabled() = sharedPreferences.getBoolean(AppConstants.OTP_ENABLED, false)

    fun isSecureModeEnabled() =
        sharedPreferences.getBoolean(AppConstants.SECURE_MODE_ENABLED, false)

    fun removeTOTP() {
        viewModelScope.launch(Dispatchers.IO) {
            removeTotpUseCase().collect {
                removeTotpLiveData.postValue(Event(it))
            }
        }
    }
}