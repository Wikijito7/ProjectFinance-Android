package es.wokis.projectfinance.utils

import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceType

fun List<InvoiceBO>.getTotalDepositAsDouble(): Double = filterByType(InvoiceType.DEPOSIT).sumOf { it.quantity.toDouble() }

fun List<InvoiceBO>.getTotalDeposit(): Int = filterByType(InvoiceType.DEPOSIT).sumOf { it.quantity }

fun List<InvoiceBO>.getTotalExpenseAsDouble(): Double = filterByType(InvoiceType.EXPENSE).sumOf { it.quantity.toDouble() }

fun List<InvoiceBO>.getTotalExpense(): Int = filterByType(InvoiceType.EXPENSE).sumOf { it.quantity }

fun List<InvoiceBO>.getTotalSaved() = getTotalDeposit() + getTotalExpense()

fun List<InvoiceBO>.getTotalSavedAsDouble(): Double = getTotalDepositAsDouble() + getTotalExpenseAsDouble()

private fun List<InvoiceBO>.filterByType(type: InvoiceType) = this.filter { it.type ==  type }