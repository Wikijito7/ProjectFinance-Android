package es.wokis.projectfinance.ui.home.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.data.constants.AppConstants
import es.wokis.projectfinance.data.domain.invoice.GetInvoiceBetweenUseCase
import es.wokis.projectfinance.data.domain.invoice.GetInvoiceUseCase
import es.wokis.projectfinance.data.domain.invoice.GetLastInvoicesUseCase
import es.wokis.projectfinance.data.domain.invoice.InsertRemovedInvoiceUseCase
import es.wokis.projectfinance.ui.home.mapper.toHomeVO
import es.wokis.projectfinance.ui.home.vo.HomeInvoiceVO
import es.wokis.projectfinance.utils.toDate
import es.wokis.projectfinance.utils.toMonetaryFloat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getInvoiceBetweenUseCase: GetInvoiceBetweenUseCase,
    private val getLastInvoicesUseCase: GetLastInvoicesUseCase,
    private val insertRemovedInvoiceUseCase: InsertRemovedInvoiceUseCase,
    private val getInvoicesUseCase: GetInvoiceUseCase
) : ViewModel() {
    // region private livedata
    private val invoices: MutableLiveData<List<InvoiceBO>> = MutableLiveData()
    private val allInvoices: MutableLiveData<List<InvoiceBO>> = MutableLiveData()
    private val homeInvoices: MutableLiveData<List<HomeInvoiceVO>> = MutableLiveData()
    private val isInvoiceReinserted: MutableLiveData<Boolean> = MutableLiveData()
    // endregion

    // region public livedata
    fun getInvoicesLiveData() = invoices as LiveData<List<InvoiceBO>>
    fun getHomeInvoicesLiveData() = homeInvoices as LiveData<List<HomeInvoiceVO>>
    fun getAllInvoicesLiveData() = allInvoices as LiveData<List<InvoiceBO>>
    // endregion

    fun getInvoices() {
        val (startDate, endDate) = getStartAndEndDate()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getInvoiceBetweenUseCase(startDate, endDate).collect {
                    invoices.postValue(it)
                }

            } catch (e: Throwable) {
                Log.d("TAG", "getInvoices: ${e.stackTraceToString()}")
            }
        }
    }

    fun getAllInvoices() {
        viewModelScope.launch(Dispatchers.IO) {
            getInvoicesUseCase().collect {
                allInvoices.postValue(it)
            }
        }
    }

    fun getLastInvoices() {
        viewModelScope.launch(Dispatchers.IO) {
            getLastInvoicesUseCase(NUMBER_OF_INVOICES).collect {
                homeInvoices.postValue(it.toHomeVO())
            }
        }
    }

    fun reinsertRemovedInvoice() {
        viewModelScope.launch(Dispatchers.IO) {
            val invoiceReinserted = insertRemovedInvoiceUseCase()
            isInvoiceReinserted.postValue(invoiceReinserted)
        }
    }

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

    fun generateExpensesPerCategoryPieData(invoices: List<InvoiceBO>): PieData? {
        val expenses = invoices.filter {
            it.type == InvoiceType.EXPENSE
        }
        val sumOfExpenses = expenses.sumOf { it.quantity.absoluteValue.toDouble() }
        val pieEntries = expenses.groupBy {
            it.category?.title
        }.map {
            Pair(
                it.key, it.value.sumOf { invoice ->
                    invoice.quantity.absoluteValue.toDouble()
                }
            )
        }.sortedByDescending {
            (it.second / sumOfExpenses).toFloat()
        }.map {
            PieEntry(
                (it.second / sumOfExpenses).toFloat(),
                AppConstants.EMPTY_TEXT
            )
        }
        return PieData(
            PieDataSet(
                pieEntries,
                "Categories"
            ).apply {
                colors = getPieChartColors(invoices.filter { it.type == InvoiceType.EXPENSE })
                setDrawValues(false)
            }
        )
    }

    fun generateDepositPerCategoryPieData(invoices: List<InvoiceBO>): PieData? {
        val deposits = invoices.filter {
            it.type == InvoiceType.DEPOSIT
        }
        val sumOfDeposits = deposits.sumOf { it.quantity.toDouble() }
        val pieEntries = deposits.groupBy {
            it.category?.title
        }.map {
            Pair(
                it.key, it.value.sumOf { invoice ->
                    invoice.quantity.absoluteValue.toDouble()
                }
            )
        }.sortedByDescending {
            (it.second / sumOfDeposits).toFloat()
        }.map {
            PieEntry(
                (it.second / sumOfDeposits).toFloat(),
                AppConstants.EMPTY_TEXT
            )
        }
        return PieData(
            PieDataSet(
                pieEntries,
                "Categories"
            ).apply {
                colors = getPieChartColors(invoices.filter { it.type == InvoiceType.DEPOSIT })
                setDrawValues(false)
            }
        )
    }

    fun generateSavingsHistoricalData(invoices: List<InvoiceBO>, drawColor: Int): LineData? {
        val lineEntries = invoices.groupBy {
            it.groupByDate()
        }.map {
            Pair(
                "${it.key.second}/${it.key.first}", it.value.sumOf { invoice ->
                    invoice.quantity.toDouble()
                }
            )
        }.sortedBy {
            it.first
        }.mapIndexed { index, value ->
            Entry(index.toFloat(), value.second.toFloat().toMonetaryFloat())
        }
        val lineDataSet = LineDataSet(lineEntries, "Test").apply {
            color = drawColor
            circleColors = listOf(drawColor)
            circleHoleColor = drawColor
            setDrawValues(false)
        }
        return LineData(lineDataSet)
    }

    private fun InvoiceBO.groupByDate(): Pair<Int, Int> {
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        val month: Int = cal.get(Calendar.MONTH)
        val year: Int = cal.get(Calendar.YEAR)
        return Pair(month, year)
    }

    private fun getPieChartColors(invoices: List<InvoiceBO>): List<Int> {
        return invoices
            .groupBy { it.category }
            .map {
                Pair(
                    it.key?.color, it.value.sumOf { invoice ->
                        invoice.quantity.toDouble()
                    }
                )
            }.sortedByDescending {
                it.second.absoluteValue
            }.map {
                Color.parseColor(it.first)
            }
    }

    companion object {
        private const val NUMBER_OF_INVOICES = 4
    }
}
