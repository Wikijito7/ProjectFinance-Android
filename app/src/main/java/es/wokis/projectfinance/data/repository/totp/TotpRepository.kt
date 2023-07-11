package es.wokis.projectfinance.data.repository.totp

import es.wokis.projectfinance.data.bo.totp.TOTPResponseBO
import es.wokis.projectfinance.data.datasource.totp.TotpRemoteDataSource
import es.wokis.projectfinance.data.error.RepositoryErrorManager
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface TotpRepository {
    suspend fun activateTotp(): Flow<AsyncResult<TOTPResponseBO>>
    suspend fun removeTotp(): Flow<AsyncResult<Boolean>>
}

class TotpRepositoryImpl(
    private val remoteDataSource: TotpRemoteDataSource,
    private val repositoryErrorManager: RepositoryErrorManager
) : TotpRepository {

    override suspend fun activateTotp(): Flow<AsyncResult<TOTPResponseBO>> = repositoryErrorManager.wrap {
            remoteDataSource.activateTotp()
        }

    override suspend fun removeTotp(): Flow<AsyncResult<Boolean>> = repositoryErrorManager.wrap {
        remoteDataSource.removeTotp()
    }

}