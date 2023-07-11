package es.wokis.projectfinance.ui.graphs.viewmodel

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
import es.wokis.projectfinance.data.domain.invoice.GetInvoiceBetweenUseCase
import es.wokis.projectfinance.data.domain.invoice.GetInvoiceUseCase
import es.wokis.projectfinance.ui.graphs.enums.GraphTypeEnum
import es.wokis.projectfinance.ui.graphs.vo.GraphDataVO
import es.wokis.projectfinance.utils.asPercentage
import es.wokis.projectfinance.utils.toDate
import es.wokis.projectfinance.utils.toMonetaryDouble
import es.wokis.projectfinance.utils.toMonetaryFloat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class GraphsViewModel @Inject constructor(
    private val getInvoiceBetweenUseCase: GetInvoiceBetweenUseCase,
    private val getInvoicesUseCase: GetInvoiceUseCase
) : ViewModel() {

    private val invoices: MutableLiveData<List<InvoiceBO>> = MutableLiveData()
    private val allInvoices: MutableLiveData<List<InvoiceBO>> = MutableLiveData()

    fun getInvoicesLiveData() = invoices as LiveData<List<InvoiceBO>>
    fun getAllInvoicesLiveData() = allInvoices as LiveData<List<InvoiceBO>>

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

    fun getDepositsGraphData(invoices: List<InvoiceBO>): List<GraphDataVO> {
        val deposits = getDeposits(invoices)
        val totalOfDeposits = getTotalSumOfInvoices(deposits)
        val depositsPerCategory = getAmountPerCategory(deposits)
        return depositsPerCategory.map {
            val categoryColor =
                deposits.find { deposit -> deposit.category?.title == it.first }?.category?.color
            GraphDataVO(
                it.first.orEmpty(),
                it.second.toMonetaryDouble(),
                ((it.second / totalOfDeposits) * 100).asPercentage(),
                categoryColor
            )
        }.sortedByDescending {
            it.amount
        }
    }

    fun getExpensesGraphData(invoices: List<InvoiceBO>): List<GraphDataVO> {
        val expenses = getExpenses(invoices)
        val totalOfExpenses = getTotalSumOfInvoices(expenses)
        val expensesPerCategory = getAmountPerCategory(expenses)
        return expensesPerCategory.map {
            val categoryColor =
                expenses.find { expense -> expense.category?.title == it.first }?.category?.color
            GraphDataVO(
                it.first.orEmpty(),
                it.second.toMonetaryDouble(),
                ((it.second / totalOfExpenses) * 100).asPercentage(),
                categoryColor
            )
        }.sortedByDescending {
            it.amount
        }
    }

    fun getHistoricalGraphData(invoices: List<InvoiceBO>, locale: Locale): List<GraphDataVO> =
        invoices.groupBy {
            it.groupByDate()
        }.map {
            Pair(
                it.key,
                it.value.sumOf { invoice -> invoice.quantity.toDouble() }.toMonetaryDouble()
            )
        }.sortedBy {
            "${it.first.second}/${it.first.first}"
        }.map {
            val date =
                SimpleDateFormat("M/yyyy", locale).parse("${it.first.first + 1}/${it.first.second}")
            val dateFormatted = date?.let {
                SimpleDateFormat("MMMM, yyyy", locale).format(date)
            }.orEmpty()
            GraphDataVO(
                dateFormatted,
                amount = it.second
            )
        }

    fun generateExpensesPerCategoryPieData(invoices: List<InvoiceBO>): PieData? {
        val expenses = getExpenses(invoices)
        val sumOfExpenses = getTotalSumOfInvoices(expenses)
        val pieEntries = getAmountPerCategory(expenses).sortedByDescending {
            (it.second / sumOfExpenses).toFloat()
        }.map {
            PieEntry(
                (it.second / sumOfExpenses).toFloat(),
                it.first
            )
        }
        return PieData(
            PieDataSet(
                pieEntries,
                "Categories"
            ).apply {
                colors = getPieChartColors(getExpenses(invoices))
                setDrawValues(false)
            }
        )
    }

    fun generateDepositPerCategoryPieData(invoices: List<InvoiceBO>): PieData? {
        val deposits = getDeposits(invoices)
        val sumOfDeposits = deposits.sumOf { it.quantity.toDouble() }
        val pieEntries = getAmountPerCategory(deposits).sortedByDescending {
            (it.second / sumOfDeposits).toFloat()
        }.map {
            PieEntry(
                (it.second / sumOfDeposits).toFloat(),
                it.first
            )
        }
        return PieData(
            PieDataSet(
                pieEntries,
                "Categories"
            ).apply {
                colors = getPieChartColors(getDeposits(invoices))
                setDrawValues(false)
            }
        )
    }

    fun generateSavingsHistoricalData(invoices: List<InvoiceBO>, drawColor: Int): LineData? {
        val lineEntries = getInvoicesByDate(invoices).mapIndexed { index, value ->
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

    fun getDisplayDates(invoices: List<InvoiceBO>) = invoices.groupBy {
        it.groupByDate()
    }.map {
        it.key
    }.sortedBy {
        "${it.second}/${it.first}"
    }.map {
        "${it.first + 1}/${it.second}" // date is from 0 to 11...
    }

    fun getGraphDataTypes(): List<String> = listOf(HISTORICAL, CATEGORY_DEPOSIT, CATEGORY_EXPENSES)

    fun getGraphTypes(): List<GraphTypeEnum> =
        listOf(GraphTypeEnum.BAR_GRAPH, GraphTypeEnum.PIE_CHART, GraphTypeEnum.PIE_CHART)

    fun shouldRequestAllInvoices(tabType: String): Boolean = tabType == HISTORICAL

    fun isCategoryDeposit(tabType: String): Boolean = tabType == CATEGORY_DEPOSIT

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

    private fun getInvoicesByDate(invoices: List<InvoiceBO>) =
        invoices.groupBy {
            it.groupByDate()
        }.map {
            Pair(
                "${it.key.second}/${it.key.first}", it.value.sumOf { invoice ->
                    invoice.quantity.toDouble()
                }
            )
        }.sortedBy {
            it.first
        }

    private fun getExpenses(invoices: List<InvoiceBO>) =
        invoices.filter {
            it.type == InvoiceType.EXPENSE
        }

    private fun getDeposits(invoices: List<InvoiceBO>) =
        invoices.filter {
            it.type == InvoiceType.DEPOSIT
        }

    private fun getAmountPerCategory(invoices: List<InvoiceBO>) =
        invoices.groupBy {
            it.category?.title
        }.map {
            Pair(
                it.key, getTotalSumOfInvoices(it.value)
            )
        }

    private fun getTotalSumOfInvoices(invoices: List<InvoiceBO>) =
        invoices.sumOf { it.quantity.absoluteValue.toDouble() }

    private fun InvoiceBO.groupByDate(): Pair<Int, Int> {
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        val month: Int = cal.get(Calendar.MONTH)
        val year: Int = cal.get(Calendar.YEAR)
        return Pair(month, year)
    }

    companion object {
        private const val HISTORICAL = "HISTORICAL"
        private const val CATEGORY_DEPOSIT = "CATEGORY_DEPOSIT"
        private const val CATEGORY_EXPENSES = "CATEGORY_EXPENSES"
    }
}

