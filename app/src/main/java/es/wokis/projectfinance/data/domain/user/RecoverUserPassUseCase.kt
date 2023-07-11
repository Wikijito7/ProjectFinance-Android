package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.Flow

interface RecoverUserPassUseCase {
    suspend operator fun invoke(recoverCode: String, newPass: String): Flow<AsyncResult<Boolean>>
}

class RecoverUserPassUseCaseImpl(private val userRepository: UserRepository) :
    RecoverUserPassUseCase {

    override suspend fun invoke(recoverCode: String, newPass: String): Flow<AsyncResult<Boolean>> =
        userRepository.recoverUserPass(recoverCode, newPass)

}