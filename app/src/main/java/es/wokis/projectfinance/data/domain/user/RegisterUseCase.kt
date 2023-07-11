package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.bo.user.AuthUserBO
import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface RegisterUseCase {
    suspend operator fun invoke(user: AuthUserBO): Flow<AsyncResult<Boolean>>
}

class RegisterUseCaseImpl(private val userRepository: UserRepository) : RegisterUseCase {

    override suspend fun invoke(user: AuthUserBO) = userRepository.register(user)

}