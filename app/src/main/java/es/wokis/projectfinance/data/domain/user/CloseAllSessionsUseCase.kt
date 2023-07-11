package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface CloseAllSessionsUseCase {
    suspend operator fun invoke(): Flow<AsyncResult<Boolean>>
}

class CloseAllSessionsUseCaseImpl(private val repository: UserRepository) : CloseAllSessionsUseCase {

    override suspend fun invoke() = repository.closeAllSessions()

}