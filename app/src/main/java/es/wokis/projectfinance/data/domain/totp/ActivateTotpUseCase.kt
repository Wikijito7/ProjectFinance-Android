package es.wokis.projectfinance.data.domain.totp

import es.wokis.projectfinance.data.bo.totp.TOTPResponseBO
import es.wokis.projectfinance.data.repository.totp.TotpRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface ActivateTotpUseCase {
    suspend operator fun invoke(): Flow<AsyncResult<TOTPResponseBO>>
}

class ActivateTotpUseCaseImpl(private val repository: TotpRepository) : ActivateTotpUseCase {

    override suspend fun invoke(): Flow<AsyncResult<TOTPResponseBO>> = repository.activateTotp()

}