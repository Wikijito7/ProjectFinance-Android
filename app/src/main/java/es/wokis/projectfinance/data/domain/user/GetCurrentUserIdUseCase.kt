package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository

interface GetCurrentUserIdUseCase {
    operator fun invoke(): String?
}

class GetCurrentUserIdUseCaseImpl(private val repository: UserRepository) : GetCurrentUserIdUseCase {

    override fun invoke(): String? = repository.getCurrentUserId()

}