package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface RequestRecoverPassUseCase {
    suspend operator fun invoke(email: String): Flow<AsyncResult<Boolean>>
}

class RequestRecoverPassUseCaseImpl(private val repository: UserRepository) :
    RequestRecoverPassUseCase {

    override suspend fun invoke(email: String): Flow<AsyncResult<Boolean>> =
        repository.requestRecoverPass(email)

}