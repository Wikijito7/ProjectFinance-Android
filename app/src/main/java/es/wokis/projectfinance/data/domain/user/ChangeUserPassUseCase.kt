package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface ChangeUserPassUseCase {
    suspend operator fun invoke(oldPass: String, newPass: String): Flow<AsyncResult<Boolean>>
}

class ChangeUserPassUseCaseImpl(private val userRepository: UserRepository): ChangeUserPassUseCase {

    override suspend fun invoke(oldPass: String, newPass: String): Flow<AsyncResult<Boolean>> = userRepository.changeUserPass(oldPass, newPass)

}