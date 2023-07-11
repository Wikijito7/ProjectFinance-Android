package es.wokis.projectfinance.ui.addinvoice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_CATEGORY_ID
import es.wokis.projectfinance.data.domain.category.GetCategoryByIdUseCase
import es.wokis.projectfinance.data.domain.invoice.AddInvoiceUseCase
import es.wokis.projectfinance.data.domain.invoice.GetInvoiceByIdUseCase
import es.wokis.projectfinance.data.domain.invoice.UpdateInvoiceUseCase
import es.wokis.projectfinance.data.domain.user.GetCurrentUserIdUseCase
import es.wokis.projectfinance.utils.orZero
import es.wokis.projectfinance.utils.toCents
import es.wokis.projectfinance.utils.toDate
import es.wokis.projectfinance.utils.toStringFormatted
import es.wokis.projectfinance.utils.withSign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddInvoiceViewModel @Inject constructor(
    private val addInvoiceUseCase: AddInvoiceUseCase,
    private val updateInvoiceUseCase: UpdateInvoiceUseCase,
    private val getInvoiceByIdUseCase: GetInvoiceByIdUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {
    // region private livedata
    private val addInvoiceResultLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val getInvoiceLiveData: MutableLiveData<InvoiceBO> = MutableLiveData()
    // endregion

    private var category: CategoryBO? = null
    private var serverId: String? = null
    var invoiceType: InvoiceType = InvoiceType.DEPOSIT

    // region public live data
    fun getAddInvoiceResultLiveData() = addInvoiceResultLiveData as LiveData<Boolean>
    fun getInvoiceLiveData() = getInvoiceLiveData as LiveData<InvoiceBO>
    // endregion

    fun getInvoice(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val invoice = getInvoiceByIdUseCase(id)
            category = invoice.category
            serverId = invoice.serverId
            getInvoiceLiveData.postValue(invoice)
        }
    }

    fun addInvoice(
        id: Long,
        title: String,
        description: String,
        quantity: Float,
        date: String,
        synchronize: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = getCurrentUserIdUseCase()
            val newInvoice = id == 0L
            val invoice = InvoiceBO(
                id = id,
                serverId = serverId,
                title = title,
                description = description.trim(),
                quantity = quantity.toCents().withSign(invoiceType),
                date = date.toDate(),
                type = invoiceType,
                category = getCategory(),
                userId = currentUserId,
                updated = newInvoice.not(),
                synchronize = synchronize
            )
            val inserted = if (newInvoice) {
                addInvoiceUseCase(invoice)

            } else {
                updateInvoiceUseCase(invoice)
            }
            addInvoiceResultLiveData.postValue(inserted)
        }
    }

    private suspend fun getCategory(): CategoryBO? = category?.let {
        getCategoryByIdUseCase(it.id)
    } ?: getCategoryByIdUseCase(DEFAULT_CATEGORY_ID)

    fun saveCategoryId(categoryId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentCategory = getCategoryByIdUseCase(categoryId)
            category = currentCategory
        }
    }

    fun getCategoryId(): Long = category?.id.orZero()

    fun getTodayDate(): String = Date().toStringFormatted()
}
