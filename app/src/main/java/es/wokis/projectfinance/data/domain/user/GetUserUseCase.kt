package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.bo.user.UserBO
import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface GetUserUseCase {
    suspend operator fun invoke(): Flow<AsyncResult<UserBO>>
}

class GetUserUseCaseImpl(private val userRepository: UserRepository) : GetUserUseCase {

    override suspend fun invoke(): Flow<AsyncResult<UserBO>> = userRepository.getUser()

}