package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.bo.user.AuthUserBO
import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.data.response.ErrorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface LoginUseCase {
    suspend operator fun invoke(user: AuthUserBO): Flow<AsyncResult<Boolean>>
}

class LoginUseCaseImpl(private val userRepository: UserRepository) : LoginUseCase {

    override suspend fun invoke(user: AuthUserBO) = flow {
        userRepository.login(user).collect {
            if (it is AsyncResult.Error) {
                when (it.error) {
                    is ErrorType.ServerError -> {
                        emit(
                            AsyncResult.Error(
                                when (it.error.httpCode) {
                                    404 -> WrongUsernameOrPasswordError(it.error.errorMessage)
                                    409 -> UserAlreadyExistsError(it.error.errorMessage)
                                    else -> it.error
                                },
                                it.data
                            )
                        )

                    }
                    else -> emit(it)
                }

            } else {
                emit(it)
            }
        }
    }
}

class WrongUsernameOrPasswordError(errorMessage: String) : ErrorType.CustomError(errorMessage)

class UserAlreadyExistsError(errorMessage: String) : ErrorType.CustomError(errorMessage)