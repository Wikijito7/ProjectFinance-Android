package es.wokis.projectfinance.data.datasource.reaction

import es.wokis.projectfinance.data.bo.reaction.ReactionBO

interface ReactionLocalDataSource {
    suspend fun addReaction(reaction: ReactionBO): Boolean
    suspend fun removeReaction(reactionId: Long): Boolean
    suspend fun removeReactionsWithoutInvoice(): Boolean
    suspend fun getMostUsedReactions(): List<String>
}