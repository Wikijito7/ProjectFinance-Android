package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.data.response.ErrorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface LoginWithGoogleUseCase {
    suspend operator fun invoke(authToken: String): Flow<AsyncResult<Boolean>>
}

class LoginWithGoogleUseCaseImpl(private val userRepository: UserRepository) : LoginWithGoogleUseCase {

    override suspend fun invoke(authToken: String) = flow {
        userRepository.loginWithGoogle(authToken).collect {
        if (it is AsyncResult.Error) {
            when (it.error) {
                is ErrorType.ServerError -> {
                    when (it.error.httpCode) {
                        404 -> emit(AsyncResult.Error(WrongUsernameOrPasswordError(it.error.errorMessage), false))
                        else -> emit(it)
                    }
                }
                else -> emit(it)
            }

        } else {
            emit(it)
        }
    }
}

}