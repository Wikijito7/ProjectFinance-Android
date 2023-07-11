package es.wokis.projectfinance.ui.reaction.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.data.domain.invoice.GetInvoiceByIdUseCase
import es.wokis.projectfinance.data.domain.reaction.ReinsertRemovedReactionUseCase
import es.wokis.projectfinance.data.domain.reaction.RemoveReactionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReactionDetailViewModel @Inject constructor(
    private val removeReactionUseCase: RemoveReactionUseCase,
    private val reinsertRemovedReactionUseCase: ReinsertRemovedReactionUseCase,
    private val getInvoiceByIdUseCase: GetInvoiceByIdUseCase
) : ViewModel() {

    private val reactionsLiveData: MutableLiveData<List<ReactionBO>> = MutableLiveData()
    private val removeReactionLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var invoiceId: Long = 0

    fun getReactionsLiveData() = reactionsLiveData as LiveData<List<ReactionBO>>
    fun getRemoveReactionLiveData() = removeReactionLiveData as LiveData<Boolean>

    fun getInvoice(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            invoiceId = id
            val invoice = getInvoiceByIdUseCase(id)
            reactionsLiveData.postValue(invoice.reactions)
        }
    }

    fun removeReaction(reaction: ReactionBO) {
        viewModelScope.launch(Dispatchers.IO) {
            removeReactionUseCase(reaction).also {
                getInvoice(invoiceId)
                removeReactionLiveData.postValue(it)
            }
        }
    }

    fun reinsertRemovedReaction() {
        viewModelScope.launch(Dispatchers.IO) {
            reinsertRemovedReactionUseCase().also {
                getInvoice(invoiceId)
            }
        }
    }

}