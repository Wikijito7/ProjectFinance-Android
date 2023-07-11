package es.wokis.projectfinance.ui.category.viewmodel

import androidx.lifecycle.ViewModel
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_COLOR

class ColorPickerViewModel : ViewModel() {
    var lastColor: String = DEFAULT_COLOR
}