package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface RequestVerificationEmailUseCase {
    suspend operator fun invoke(): Flow<AsyncResult<Boolean>>
}

class RequestVerificationEmailUseCaseImpl(
    private val repository: UserRepository
) : RequestVerificationEmailUseCase {

    override suspend fun invoke(): Flow<AsyncResult<Boolean>> = repository.requestVerificationEmail()

}