package es.wokis.projectfinance.data.domain.user

import android.content.SharedPreferences
import es.wokis.projectfinance.data.constants.AppConstants

interface GetResendTimestampUseCase {
    operator fun invoke(): Long
}

class GetResendTimestampUseCaseImpl(
    private val sharedPreferences: SharedPreferences
) : GetResendTimestampUseCase {

    override fun invoke(): Long = sharedPreferences.getLong(AppConstants.RESEND_EMAIL_TIMESTAMP, 0)

}