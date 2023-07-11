package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository

interface SaveCurrentUserIdUseCase {
    operator fun invoke(id: String): Boolean
}

class SaveCurrentUserIdUseCaseImpl(private val repository: UserRepository) : SaveCurrentUserIdUseCase {

    override fun invoke(id: String): Boolean = repository.saveCurrentUserId(id)

}