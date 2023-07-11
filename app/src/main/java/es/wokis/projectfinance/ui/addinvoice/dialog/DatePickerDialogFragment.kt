package es.wokis.projectfinance.ui.addinvoice.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import es.wokis.projectfinance.ui.addinvoice.fragment.AddInvoiceDialogFragment
import es.wokis.projectfinance.ui.base.BaseDialogFragment
import java.util.Calendar

class DatePickerFragment : BaseDialogFragment(), DatePickerDialog.OnDateSetListener {

    private val args: DatePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return context?.let { DatePickerDialog(it, this, year, month, day) }
            ?: super.onCreateDialog(savedInstanceState)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val bundle = Bundle().apply {
            putInt(AddInvoiceDialogFragment.DAY, day)
            putInt(AddInvoiceDialogFragment.MONTH, month + 1) // cause month starts at 0.. yep.
            putInt(AddInvoiceDialogFragment.YEAR, year)
        }
        setFragmentResult(args.requestKey, bundle)
        navigateBack()
    }
}