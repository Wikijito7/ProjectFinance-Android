package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.bo.user.UpdateUserBO
import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface UpdateUserUseCase {
    suspend operator fun invoke(updateUser: UpdateUserBO): Flow<AsyncResult<Boolean>>
}

class UpdateUserUseCaseImpl(private val userRepository: UserRepository) : UpdateUserUseCase {

    override suspend fun invoke(updateUser: UpdateUserBO): Flow<AsyncResult<Boolean>> =
        userRepository.updateUser(updateUser)

}