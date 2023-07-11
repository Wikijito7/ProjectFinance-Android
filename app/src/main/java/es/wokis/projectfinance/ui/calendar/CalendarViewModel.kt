package es.wokis.projectfinance.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {

    private val text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }

    fun getText() = text as LiveData<String>
}