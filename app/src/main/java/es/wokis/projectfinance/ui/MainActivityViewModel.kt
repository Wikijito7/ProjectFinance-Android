package es.wokis.projectfinance.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.constants.AppConstants
import es.wokis.projectfinance.data.domain.user.UserLoggedInUseCase
import es.wokis.projectfinance.data.enums.SelectedThemeEnum
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userLoggedInUseCase: UserLoggedInUseCase
) : ViewModel() {

    fun isUserLoggedIn() = userLoggedInUseCase()

    fun isSecureModeEnabled() = sharedPreferences.getBoolean(AppConstants.SECURE_MODE_ENABLED, false)

    fun getSelectedTheme() = SelectedThemeEnum.fromKey(sharedPreferences.getString(AppConstants.SELECTED_THEME, null))

}