package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.repository.user.UserRepository
import kotlinx.coroutines.flow.collect

interface SignOutUseCase {
    suspend operator fun invoke()
}

class SignOutUseCaseImpl(private val repository: UserRepository) : SignOutUseCase {

    override suspend fun invoke() = repository.signOut().collect()

}