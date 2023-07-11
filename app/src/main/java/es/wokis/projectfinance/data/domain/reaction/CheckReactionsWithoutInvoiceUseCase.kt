package es.wokis.projectfinance.data.domain.reaction

import es.wokis.projectfinance.data.repository.reaction.ReactionRepository

interface CheckReactionsWithoutInvoiceUseCase {
    suspend operator fun invoke(): Boolean
}

class CheckReactionsWithoutInvoiceUseCaseImpl(private val repository: ReactionRepository) :
    CheckReactionsWithoutInvoiceUseCase {

    override suspend fun invoke(): Boolean = repository.checkReactionsWithoutInvoice()

}