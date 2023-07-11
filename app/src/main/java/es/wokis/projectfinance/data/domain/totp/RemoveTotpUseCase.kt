package es.wokis.projectfinance.data.domain.totp

import es.wokis.projectfinance.data.repository.totp.TotpRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface RemoveTotpUseCase {
    suspend operator fun invoke(): Flow<AsyncResult<Boolean>>
}

class RemoveTotpUseCaseImpl(private val repository: TotpRepository) : RemoveTotpUseCase {

    override suspend fun invoke(): Flow<AsyncResult<Boolean>> = repository.removeTotp()

}