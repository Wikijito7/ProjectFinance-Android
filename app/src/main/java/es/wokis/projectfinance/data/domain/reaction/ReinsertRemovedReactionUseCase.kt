package es.wokis.projectfinance.data.domain.reaction

import es.wokis.projectfinance.data.repository.reaction.ReactionRepository

interface ReinsertRemovedReactionUseCase {
    suspend operator fun invoke(): Boolean
}

class ReinsertRemovedReactionUseCaseImpl(private val repository: ReactionRepository) : ReinsertRemovedReactionUseCase {

    override suspend fun invoke(): Boolean = repository.reinsertRemovedReaction()

}