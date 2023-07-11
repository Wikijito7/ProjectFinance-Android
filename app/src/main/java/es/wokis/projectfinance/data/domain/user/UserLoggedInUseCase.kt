package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository

interface UserLoggedInUseCase {
    operator fun invoke(): Boolean
}

class UserLoggedInUseCaseImpl(private val repository: UserRepository) : UserLoggedInUseCase {

    override fun invoke(): Boolean = repository.isUserLoggedIn()

}