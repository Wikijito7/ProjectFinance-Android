package es.wokis.projectfinance.data.domain.reaction

import es.wokis.projectfinance.data.repository.reaction.ReactionRepository

interface GetMostUsedReactionsUseCase {
    suspend operator fun invoke(): List<String>
}

class GetMostUsedReactionsUseCaseImpl(private val reactionRepository: ReactionRepository): GetMostUsedReactionsUseCase {

    override suspend fun invoke(): List<String> = reactionRepository.getMostUsedReactions()

}