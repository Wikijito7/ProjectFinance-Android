package es.wokis.projectfinance.ui.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.constants.AppConstants
import es.wokis.projectfinance.data.domain.invoice.ClearFiltersUseCase
import es.wokis.projectfinance.data.domain.invoice.GetFilteredInvoiceUseCase
import es.wokis.projectfinance.data.domain.invoice.InsertRemovedInvoiceUseCase
import es.wokis.projectfinance.data.domain.invoice.RemoveInvoiceUseCase
import es.wokis.projectfinance.ui.dashboard.mapper.toVO
import es.wokis.projectfinance.ui.dashboard.vo.InvoiceVO
import es.wokis.projectfinance.utils.applyAdsToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val insertRemovedInvoiceUseCase: InsertRemovedInvoiceUseCase,
    private val getFilteredInvoiceUseCase: GetFilteredInvoiceUseCase,
    private val removeInvoiceUseCase: RemoveInvoiceUseCase,
    private val clearFiltersUseCase: ClearFiltersUseCase
) : ViewModel() {

    // region private livedata
    private val invoicesView: MutableLiveData<List<InvoiceVO>> = MutableLiveData()
    private val invoices: MutableLiveData<List<InvoiceBO>> = MutableLiveData()
    private val deleteInvoiceLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val editInvoiceLiveData: MutableLiveData<Long> = MutableLiveData()
    // endregion

    private var invoiceList: List<InvoiceBO> = emptyList()

    // region public livedata
    fun getInvoicesVOLiveData() = invoicesView as LiveData<List<InvoiceVO>>
    fun getInvoicesLiveData() = invoices as LiveData<List<InvoiceBO>>
    fun getDeleteInvoiceLiveData() = deleteInvoiceLiveData as LiveData<Boolean>
    fun getEditInvoiceLiveData() = editInvoiceLiveData as LiveData<Long>

    fun getFilterTypes() =
        listOf(AppConstants.EMPTY_TEXT, InvoiceType.DEPOSIT.key, InvoiceType.EXPENSE.key)

    fun getInvoices() {
        viewModelScope.launch(Dispatchers.IO) {
            getFilteredInvoiceUseCase().collect {
                invoices.postValue(it)
            }
        }
    }

    fun getInvoices(filter: String = AppConstants.EMPTY_TEXT) {
        viewModelScope.launch(Dispatchers.IO) {
            getFilteredInvoiceUseCase().collect {
                val invoicesList = if (filter.isNotEmpty()) {
                    it.filter { invoice ->
                        invoice.type == InvoiceType.getFromKey(filter)
                    }

                } else {
                    it
                }.applyAdsToList()

                invoiceList = invoicesList
                invoicesView.postValue(invoicesList.toVO())
            }
        }
    }

    fun deleteInvoice(position: Int) {
        invoiceList.getOrNull(position)?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val removed = removeInvoiceUseCase(it)
                deleteInvoiceLiveData.postValue(removed)
            }
        }
    }

    fun editInvoice(position: Int) {
        invoiceList.getOrNull(position)?.let {
            editInvoiceLiveData.postValue(it.id)
        }
    }

    fun reinsertRemovedInvoice() {
        viewModelScope.launch(Dispatchers.IO) {
            insertRemovedInvoiceUseCase()
        }
    }

    fun clearFilters() {
        clearFiltersUseCase()
    }
}