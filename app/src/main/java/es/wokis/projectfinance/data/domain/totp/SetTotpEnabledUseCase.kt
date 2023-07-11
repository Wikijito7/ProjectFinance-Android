package es.wokis.projectfinance.data.domain.totp

import android.content.SharedPreferences
import androidx.core.content.edit
import es.wokis.projectfinance.data.constants.AppConstants.OTP_ENABLED

interface SetTotpEnabledUseCase {
    operator fun invoke(enabled: Boolean)
}

class SetTotpEnabledUseCaseImpl(private val sharedPreferences: SharedPreferences) : SetTotpEnabledUseCase {

    override fun invoke(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(OTP_ENABLED, enabled)
        }
    }

}