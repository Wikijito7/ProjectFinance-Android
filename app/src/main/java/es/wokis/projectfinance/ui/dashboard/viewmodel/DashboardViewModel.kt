package es.wokis.projectfinance.ui.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.data.domain.invoice.GetInvoiceBetweenUseCase
import es.wokis.projectfinance.data.domain.invoice.GetInvoiceByIdUseCase
import es.wokis.projectfinance.data.domain.invoice.GetNumberOfInvoiceUseCase
import es.wokis.projectfinance.data.domain.invoice.InsertRemovedInvoiceUseCase
import es.wokis.projectfinance.data.domain.invoice.RemoveInvoiceUseCase
import es.wokis.projectfinance.data.domain.reaction.AddReactionUseCase
import es.wokis.projectfinance.data.domain.reaction.GetMostUsedReactionsUseCase
import es.wokis.projectfinance.data.domain.user.UserLoggedInUseCase
import es.wokis.projectfinance.ui.dashboard.mapper.toVO
import es.wokis.projectfinance.ui.dashboard.vo.InvoiceVO
import es.wokis.projectfinance.utils.applyAdsToList
import es.wokis.projectfinance.utils.toDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val insertRemovedInvoiceUseCase: InsertRemovedInvoiceUseCase,
    private val getInvoiceBetweenUseCase: GetInvoiceBetweenUseCase,
    private val getInvoiceByIdUseCase: GetInvoiceByIdUseCase,
    private val addReactionUseCase: AddReactionUseCase,
    private val removeInvoiceUseCase: RemoveInvoiceUseCase,
    private val isUserLoggedInUseCase: UserLoggedInUseCase,
    private val getMostUsedReactionsUseCase: GetMostUsedReactionsUseCase,
    private val getNumberOfInvoiceUseCase: GetNumberOfInvoiceUseCase
) : ViewModel() {

    // region private livedata
    private val invoices: MutableLiveData<List<InvoiceVO>> = MutableLiveData()
    private val numberOfInvoices: MutableLiveData<Int> = MutableLiveData()
    private val deleteInvoiceLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val editInvoiceLiveData: MutableLiveData<Long> = MutableLiveData()
    // endregion

    private var invoiceList: List<InvoiceBO> = emptyList()
    private var selectedInvoice: Long? = null
    private var mostUsedReactions: List<String> = emptyList()

    // region public livedata
    fun getInvoicesLiveData() = invoices as LiveData<List<InvoiceVO>>
    fun getDeleteInvoiceLiveData() = deleteInvoiceLiveData as LiveData<Boolean>
    fun getEditInvoiceLiveData() = editInvoiceLiveData as LiveData<Long>
    fun getNumberOfInvoicesLiveData() = numberOfInvoices as LiveData<Int>

    fun getInvoices(filter: String = EMPTY_TEXT) {
        val (startDate, endDate) = getStartAndEndDate()
        viewModelScope.launch(Dispatchers.IO) {
            getInvoiceBetweenUseCase(startDate, endDate).collect {
                val invoicesList = if (filter.isNotEmpty()) {
                    it.filter { invoice ->
                        invoice.type == InvoiceType.getFromKey(filter)
                    }

                } else {
                    it
                }.applyAdsToList()

                invoiceList = invoicesList
                invoices.postValue(invoicesList.toVO())
            }
        }
    }

    fun getNumberOfInvoices() {
        viewModelScope.launch(Dispatchers.IO) {
            getNumberOfInvoiceUseCase().collect {
                numberOfInvoices.postValue(it)
            }
        }
    }

    fun reinsertRemovedInvoice() {
        viewModelScope.launch(Dispatchers.IO) {
            insertRemovedInvoiceUseCase()
        }
    }

    fun getDashboardTypes() = listOf(EMPTY_TEXT, InvoiceType.DEPOSIT.key, InvoiceType.EXPENSE.key)

    fun addReaction(unicode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            selectedInvoice?.let {
                val invoice = getInvoiceByIdUseCase(it)
                if (invoice.reactions.size > 7 ||
                    invoice.reactions.any { reaction -> reaction.unicode == unicode }
                ) {
                    return@let
                }
                addReactionUseCase(
                    ReactionBO(
                        unicode = unicode,
                        invoiceId = invoice.id
                    )
                )
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

    fun setSelectedInvoice(id: Long) {
        selectedInvoice = id
    }

    fun updateMostUsedReactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val mostUsedReactions = getMostUsedReactionsUseCase()
            setMostUsedReactionsList(mostUsedReactions)
        }
    }

    private fun setMostUsedReactionsList(mostUsedReactions: List<String>) {
        this.mostUsedReactions = mostUsedReactions
    }

    fun getMostUsedReactionsList() = mostUsedReactions

    fun isUserLoggedIn() = isUserLoggedInUseCase()

    private fun getStartAndEndDate(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // remember, from 0 to 11 cause why not
        val year = calendar.get(Calendar.YEAR)
        val startDate = "$firstDay/$month/$year".toDate().time
        val endDate = "$lastDay/$month/$year".toDate().time
        return Pair(startDate, endDate)
    }
}