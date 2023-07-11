package es.wokis.projectfinance.ui.filter.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_DATE_FROM
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_DATE_TO
import es.wokis.projectfinance.data.constants.RequestKeys
import es.wokis.projectfinance.databinding.DialogFilterDateBinding
import es.wokis.projectfinance.ui.addinvoice.fragment.AddInvoiceDialogFragment
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.utils.getDateFormatted

class DateFilterDialogFragment : BaseBottomSheetDialog() {

    private val args: DateFilterDialogFragmentArgs by navArgs()

    private var binding: DialogFilterDateBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogFilterDateBinding.inflate(layoutInflater, container, false)
        setUpView()
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFragmentResultListener()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpFragmentResultListener() {
        setUpDateFromListener()
        setUpDateToListener()
    }

    private fun setUpView() {
        val dateFrom = args.selectedDateFrom
        val dateTo = args.selectedDateTo
        binding?.apply {
            dateFrom.takeIf { it.isNotBlank() }?.let {
                filterDateInputFrom.setText(it)
                filterDateBtnAccept.isEnabled = true
            }

            dateTo.takeIf { it.isNotBlank() }?.let {
                filterDateInputTo.setText(it)
            }
        }
    }

    private fun setUpDateFromListener() {
        setFragmentResultListener(RequestKeys.DATE_FILTER_TO_CALENDAR_FROM) { _, bundle ->
            val day = bundle.getInt(AddInvoiceDialogFragment.DAY)
            val month = bundle.getInt(AddInvoiceDialogFragment.MONTH)
            val year = bundle.getInt(AddInvoiceDialogFragment.YEAR)
            updateFromDate(day, month, year)
        }
    }

    private fun updateFromDate(day: Int, month: Int, year: Int) {
        binding?.filterDateInputFrom?.setText(getDateFormatted(day, month, year))
    }

    private fun setUpDateToListener() {
        setFragmentResultListener(RequestKeys.DATE_FILTER_TO_CALENDAR_TO) { _, bundle ->
            val day = bundle.getInt(AddInvoiceDialogFragment.DAY)
            val month = bundle.getInt(AddInvoiceDialogFragment.MONTH)
            val year = bundle.getInt(AddInvoiceDialogFragment.YEAR)
            updateToDate(day, month, year)
        }
    }

    private fun updateToDate(day: Int, month: Int, year: Int) {
        binding?.filterDateInputTo?.setText(getDateFormatted(day, month, year))
    }

    private fun onTextChanged(v: EditText) {
        v.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no-op
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding?.filterDateBtnAccept?.isEnabled = getFromDate().isNotBlank()
            }

            override fun afterTextChanged(p0: Editable?) {
                // no-op
            }

        })
    }

    private fun getFromDate() = binding?.filterDateInputFrom?.text.toString()

    private fun getToDate(): String = binding?.filterDateInputTo?.text.toString()

    private fun setUpListeners() {
        binding?.apply {
            filterDateBtnBack.setOnClickListener {
                navigateBack()
            }

            filterDateInputFrom.setOnClickListener {
                navigateTo(
                    DateFilterDialogFragmentDirections.actionDateFilterToDatePicker(
                        RequestKeys.DATE_FILTER_TO_CALENDAR_FROM
                    )
                )
            }

            filterDateInputTo.setOnClickListener {
                navigateTo(
                    DateFilterDialogFragmentDirections.actionDateFilterToDatePicker(
                        RequestKeys.DATE_FILTER_TO_CALENDAR_TO
                    )
                )
            }

            filterDateBtnAccept.setOnClickListener {
                onAccept()
            }
            onTextChanged(filterDateInputFrom)
        }
    }

    private fun onAccept() {
        val bundle = Bundle().apply {
            putString(FILTER_DATE_FROM, getFromDate())
            putString(FILTER_DATE_TO, getToDate())
        }
        setFragmentResult(RequestKeys.FILTER_TO_DATE_FILTER, bundle)
        navigateBack()
    }


}