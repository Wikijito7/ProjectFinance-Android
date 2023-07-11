package es.wokis.projectfinance.data.domain.user

import es.wokis.projectfinance.data.bo.user.BadgeBO
import es.wokis.projectfinance.data.repository.user.UserRepository

interface GetBadgesUseCase {
    operator fun invoke(): List<BadgeBO>
}

class GetBadgesUseCaseImpl(private val userRepository: UserRepository) : GetBadgesUseCase {

    override fun invoke(): List<BadgeBO> = userRepository.getBadges()

}