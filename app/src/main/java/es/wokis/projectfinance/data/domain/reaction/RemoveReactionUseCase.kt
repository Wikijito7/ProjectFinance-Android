package es.wokis.projectfinance.data.domain.reaction

import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.data.repository.reaction.ReactionRepository

interface RemoveReactionUseCase {
    suspend operator fun invoke(reaction: ReactionBO, synchronize: Boolean = true): Boolean
}

class RemoveReactionUseCaseImpl(private val repository: ReactionRepository) : RemoveReactionUseCase {

    override suspend fun invoke(reaction: ReactionBO, synchronize: Boolean): Boolean = repository.removeReaction(reaction, synchronize)

}