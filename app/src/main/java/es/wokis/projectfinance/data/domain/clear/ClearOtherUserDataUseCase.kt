package es.wokis.projectfinance.data.domain.clear

import es.wokis.projectfinance.data.domain.category.DeleteCategoryUseCase
import es.wokis.projectfinance.data.domain.invoice.GetInvoicesOfOtherUserUseCase
import es.wokis.projectfinance.data.domain.invoice.RemoveInvoiceUseCase
import es.wokis.projectfinance.data.domain.reaction.RemoveReactionUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface ClearOtherUserDataUseCase {
    suspend operator fun invoke()
}

class ClearOtherUserDataUseCaseImpl(
    private val getInvoicesOfOtherUserUseCase: GetInvoicesOfOtherUserUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val removeInvoiceUseCase: RemoveInvoiceUseCase,
    private val removeReactionUseCase: RemoveReactionUseCase
) : ClearOtherUserDataUseCase {

    override suspend fun invoke() {
        val invoices = getInvoicesOfOtherUserUseCase()
        coroutineScope {
            listOf<List<Deferred<Any>>>(
                invoices.flatMap { it.reactions }.map {
                    async {
                        removeReactionUseCase(it, false)
                    }
                },
                invoices.mapNotNull { it.category }.map {
                    async {
                        deleteCategoryUseCase(it)
                    }
                },
                invoices.map {
                    async {
                        removeInvoiceUseCase(it, false)
                    }
                }
            ).flatten().awaitAll()
        }
    }

}