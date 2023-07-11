package es.wokis.projectfinance.data.domain.user

import android.content.SharedPreferences
import androidx.core.content.edit
import es.wokis.projectfinance.data.constants.AppConstants

interface SetResendTimestampUseCase {
    operator fun invoke(timeStamp: Long)
}

class SetResendTimestampUseCaseImpl(
    private val sharedPreferences: SharedPreferences
) : SetResendTimestampUseCase {

    override fun invoke(timeStamp: Long) {
        sharedPreferences.edit {
            putLong(AppConstants.RESEND_EMAIL_TIMESTAMP, timeStamp)
        }
    }

}