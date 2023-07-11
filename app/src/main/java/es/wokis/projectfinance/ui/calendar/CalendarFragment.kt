package es.wokis.projectfinance.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import es.wokis.projectfinance.databinding.FragmentCalendarBinding
import es.wokis.projectfinance.ui.base.BaseFragment

class CalendarFragment : BaseFragment() {

    private var binding: FragmentCalendarBinding? = null

    private val viewModel by viewModels<CalendarViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
    }

    override fun showInvoiceRemovedSnackBar() {
        // no-op
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpObservers() {
        observeCalendar()
    }

    private fun observeCalendar() {
        viewModel
    }
}