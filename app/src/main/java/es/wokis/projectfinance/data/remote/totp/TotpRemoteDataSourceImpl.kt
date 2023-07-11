package es.wokis.projectfinance.data.remote.totp

import es.wokis.projectfinance.data.bo.totp.TOTPResponseBO
import es.wokis.projectfinance.data.datasource.totp.TotpRemoteDataSource
import es.wokis.projectfinance.data.remote.totp.mapper.toBO
import es.wokis.projectfinance.data.remote.totp.service.TotpService

class TotpRemoteDataSourceImpl(private val service: TotpService) : TotpRemoteDataSource {

    override suspend fun activateTotp(): TOTPResponseBO = service.activateTotp().toBO()

    override suspend fun removeTotp(): Boolean = service.removeTotp().acknowledge

}