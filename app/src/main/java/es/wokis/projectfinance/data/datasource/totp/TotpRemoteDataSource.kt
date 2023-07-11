package es.wokis.projectfinance.data.datasource.totp

import es.wokis.projectfinance.data.bo.totp.TOTPResponseBO

interface TotpRemoteDataSource {
    suspend fun activateTotp(): TOTPResponseBO
    suspend fun removeTotp(): Boolean
}