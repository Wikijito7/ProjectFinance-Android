package es.wokis.projectfinance.ui.addinvoice.filter

import android.text.SpannableStringBuilder

import android.text.Spanned

import android.text.method.DigitsKeyListener
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT

/**
 * It filters decimal digits.
 * Ty, https://stackoverflow.com/a/5368816
 */
class InvoiceAmountFilter : DigitsKeyListener(false, true) {
    private var digits = 2

    fun setDigits(d: Int) {
        digits = d
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        var newSource = source
        var newStart = start
        var newEnd = end
        val out = super.filter(newSource, newStart, newEnd, dest, dstart, dend)

        // if changed, replace the source
        if (out != null) {
            newSource = out
            newStart = 0
            newEnd = out.length
        }
        val len = newEnd - newStart

        // if deleting, source is empty
        // and deleting can't break anything
        if (len == 0) {
            return newSource
        }
        val destLength = dest.length

        // Find the position of the decimal .
        for (i in 0 until dstart) {
            if (dest[i] == DECIMAL_SEPARATOR) {
                // being here means, that a number has
                // been inserted after the dot
                // check if the amount of digits is right
                return if (destLength - (i + 1) + len > digits) EMPTY_TEXT else SpannableStringBuilder(
                    newSource,
                    newStart,
                    newEnd
                )
            }
        }

        for (i in newStart until newEnd) {
            if (newSource[i] == DECIMAL_SEPARATOR) {
                // being here means, dot has been inserted
                // check if the amount of digits is right
                return if (destLength - dend + (newEnd - (i + 1)) > digits) EMPTY_TEXT else break
            }
        }

        // if the dot is after the inserted part,
        // nothing can break
        return SpannableStringBuilder(newSource, newStart, newEnd)
    }

    companion object {
        private const val DECIMAL_SEPARATOR = '.'
    }
}