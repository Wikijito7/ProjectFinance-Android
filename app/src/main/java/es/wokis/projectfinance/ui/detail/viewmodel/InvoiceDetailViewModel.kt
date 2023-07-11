package es.wokis.projectfinance.ui.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.domain.invoice.GetInvoiceByIdUseCase
import es.wokis.projectfinance.data.domain.invoice.RemoveInvoiceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(
    private val getInvoiceByIdUseCase: GetInvoiceByIdUseCase,
    private val removeInvoiceUseCase: RemoveInvoiceUseCase,
) : ViewModel() {
    // region private livedata
    private val getInvoiceLiveData: MutableLiveData<InvoiceBO> = MutableLiveData()
    private val isInvoiceRemovedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val navigateToEditLiveData: MutableLiveData<Long> = MutableLiveData()
    private val navigateToEmojiSelectorLiveData: MutableLiveData<Long> = MutableLiveData()
    // endregion

    private var currentInvoice: InvoiceBO? = null

    // region public live data
    fun getInvoiceLiveData() = getInvoiceLiveData as LiveData<InvoiceBO>
    fun getIsInvoiceRemovedLiveData() = isInvoiceRemovedLiveData as LiveData<Boolean>
    fun getNavigateToEditLiveData() = navigateToEditLiveData as LiveData<Long>
    fun getNavigateToEmojiSelectorLiveData() = navigateToEmojiSelectorLiveData as LiveData<Long>
    // endregion

    fun getInvoice(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val invoice = getInvoiceByIdUseCase(id)
            currentInvoice = invoice
            getInvoiceLiveData.postValue(invoice)
        }
    }

    fun removeInvoice() {
        viewModelScope.launch(Dispatchers.IO) {
            currentInvoice?.let {
                val removed = removeInvoiceUseCase(it)
                isInvoiceRemovedLiveData.postValue(removed)
            }
        }
    }

    fun navigateToEdit(invoiceId: Long) {
        navigateToEditLiveData.postValue(invoiceId)
    }

    fun navigateToEmojiSelector(invoiceId: Long) {
        navigateToEmojiSelectorLiveData.postValue(invoiceId)
    }
}