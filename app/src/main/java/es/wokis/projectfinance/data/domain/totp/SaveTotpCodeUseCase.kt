package es.wokis.projectfinance.data.domain.totp

import android.content.SharedPreferences
import androidx.core.content.edit
import es.wokis.projectfinance.data.constants.AppConstants.TOTP
import es.wokis.projectfinance.data.constants.AppConstants.TOTP_TIMESTAMP
import java.util.Date

interface SaveTotpCodeUseCase {
    operator fun invoke(code: String)
}

class SaveTotpCodeUseCaseImpl(private val sharedPreferences: SharedPreferences) : SaveTotpCodeUseCase {

    override fun invoke(code: String) {
        sharedPreferences.edit {
            putString(TOTP, code)
            putLong(TOTP_TIMESTAMP, Date().time)
        }
    }

}