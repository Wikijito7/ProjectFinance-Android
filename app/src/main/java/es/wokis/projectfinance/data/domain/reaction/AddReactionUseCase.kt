package es.wokis.projectfinance.data.domain.reaction

import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.data.repository.reaction.ReactionRepository

interface AddReactionUseCase {
    suspend operator fun invoke(reaction: ReactionBO): Boolean
}

class AddReactionUseCaseImpl(private val repository: ReactionRepository) : AddReactionUseCase {

    override suspend fun invoke(reaction: ReactionBO): Boolean = repository.addReaction(reaction)

}