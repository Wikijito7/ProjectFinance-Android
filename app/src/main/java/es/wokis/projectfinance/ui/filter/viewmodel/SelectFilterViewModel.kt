package es.wokis.projectfinance.ui.filter.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_CATEGORIES_ID
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_CATEGORIES_NAME
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_DATE_FROM
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_DATE_TO
import es.wokis.projectfinance.data.domain.invoice.SaveFiltersUseCase
import javax.inject.Inject

@HiltViewModel
class SelectFilterViewModel @Inject constructor(
    private val saveFiltersUseCase: SaveFiltersUseCase
) : ViewModel() {

    var selectedFilters: MutableMap<String, String?> = mutableMapOf()

    fun saveCategoriesSelected(categoriesId: LongArray?, categoriesName: Array<String>?) {
        selectedFilters[FILTER_CATEGORIES_ID] = categoriesId?.joinToString(CATEGORIES_SEPARATOR)
        selectedFilters[FILTER_CATEGORIES_NAME] = categoriesName?.joinToString(CATEGORIES_SEPARATOR)
    }

    fun saveDatesSelected(dateFrom: String?, dateTo: String?) {
        selectedFilters[FILTER_DATE_FROM] = dateFrom
        selectedFilters[FILTER_DATE_TO] = dateTo?.takeIf { it.isNotBlank() } ?: dateFrom
    }

    fun deleteDateFilters() {
        selectedFilters[FILTER_DATE_FROM] = null
        selectedFilters[FILTER_DATE_TO] = null
    }

    fun deleteCategoryFilters() {
        selectedFilters[FILTER_CATEGORIES_ID] = null
        selectedFilters[FILTER_CATEGORIES_NAME] = null
    }

    fun saveFilters() {
        saveFiltersUseCase(selectedFilters.toMap())
    }

    fun getDateFrom(): String = selectedFilters[FILTER_DATE_FROM].orEmpty()

    fun getDateTo(): String {
        val dateTo = selectedFilters[FILTER_DATE_TO].orEmpty()
        return if (dateTo.isEmpty() ||
                dateTo == getDateFrom()) {
            EMPTY_TEXT

        } else {
            dateTo
        }
    }

    fun getSelectedCategoriesId() = selectedFilters[FILTER_CATEGORIES_ID].orEmpty()

    companion object {
        private const val CATEGORIES_SEPARATOR = ", "
    }
}