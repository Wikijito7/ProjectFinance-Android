package es.wokis.projectfinance.data.repository.reaction

import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.data.datasource.reaction.ReactionLocalDataSource
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository
import es.wokis.projectfinance.utils.isTrue

interface ReactionRepository {
    suspend fun addReaction(reaction: ReactionBO): Boolean
    suspend fun removeReaction(reaction: ReactionBO, synchronize: Boolean = true): Boolean
    suspend fun reinsertRemovedReaction(): Boolean
    suspend fun checkReactionsWithoutInvoice(): Boolean
    suspend fun getMostUsedReactions(): List<String>
}

class ReactionRepositoryImpl(
    private val localDataSource: ReactionLocalDataSource,
    private val invoiceRepository: InvoiceRepository
) : ReactionRepository {

    private var reactionRemoved: ReactionBO? = null

    override suspend fun addReaction(reaction: ReactionBO): Boolean {
        updateInvoice(reaction.invoiceId)
        return localDataSource.addReaction(reaction)
    }

    override suspend fun removeReaction(reaction: ReactionBO, synchronize: Boolean): Boolean {
        if (synchronize) {
            updateInvoice(reaction.invoiceId)
        }
        reactionRemoved = reaction
        return localDataSource.removeReaction(reaction.id)
    }

    override suspend fun reinsertRemovedReaction(): Boolean = reactionRemoved?.let {
        addReaction(it)
    }.isTrue()

    override suspend fun checkReactionsWithoutInvoice(): Boolean =
        localDataSource.removeReactionsWithoutInvoice()

    override suspend fun getMostUsedReactions(): List<String> = localDataSource.getMostUsedReactions()

    private suspend fun updateInvoice(invoiceId: Long): Boolean {
        val invoice = invoiceRepository.getInvoiceById(invoiceId)
        return if (invoice.synchronize) {
            invoiceRepository.updateInvoice(invoice.copy(updated = true))

        } else {
            false
        }
    }
}