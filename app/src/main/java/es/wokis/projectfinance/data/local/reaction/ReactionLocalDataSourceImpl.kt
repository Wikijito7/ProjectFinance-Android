package es.wokis.projectfinance.data.local.reaction

import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.data.dao.reaction.ReactionDAO
import es.wokis.projectfinance.data.datasource.reaction.ReactionLocalDataSource

class ReactionLocalDataSourceImpl(private val reactionDAO: ReactionDAO) : ReactionLocalDataSource {

    override suspend fun addReaction(reaction: ReactionBO): Boolean =
        reactionDAO.addReaction(reaction.toDBO()) > 0

    override suspend fun removeReaction(reactionId: Long): Boolean =
        reactionDAO.removeReaction(reactionId) > 0

    override suspend fun removeReactionsWithoutInvoice(): Boolean =
        reactionDAO.removeReactionsWithoutInvoice() > 0

    override suspend fun getMostUsedReactions(): List<String> = reactionDAO.getMostUsedEmojies()
}