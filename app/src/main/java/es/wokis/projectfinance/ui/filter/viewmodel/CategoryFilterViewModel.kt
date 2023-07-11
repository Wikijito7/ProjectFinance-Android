package es.wokis.projectfinance.ui.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.domain.category.GetCategoriesUseCase
import es.wokis.projectfinance.ui.category.mapper.toVO
import es.wokis.projectfinance.ui.category.vo.CategoryVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryFilterViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    // region private livedata
    private val categoriesLiveData: MutableLiveData<List<CategoryVO>> = MutableLiveData()
    // endregion

    private var categories: List<CategoryVO> = emptyList()
    private var selectedIds: List<Long> = emptyList()

    // region public live data
    fun getCategoriesLiveData() = categoriesLiveData as LiveData<List<CategoryVO>>
    // endregion

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            getCategoriesUseCase().collect {
                categories = it.toVO().map { category ->
                    category.copy(selected = selectedIds.contains(category.id))
                }
                categoriesLiveData.postValue(categories)
            }
        }
    }

    fun updateSelectedCategories(categorySelected: CategoryVO) {
        categories = categories.map {
            if (it.id == categorySelected.id) categorySelected.copy(selected = !it.selected) else it
        }
        categoriesLiveData.postValue(categories)
    }

    private fun getSelectedCategories() = categories.filter { it.selected }

    fun getIdOfSelectedCategories() = getSelectedCategories().map { it.id }.toLongArray()

    fun getNameOfSelectedCategories() = getSelectedCategories().map { it.title }.toTypedArray()

    fun setSelectedIds(selectedIds: String) {
        this.selectedIds = selectedIds.split(CATEGORIES_SEPARATOR).mapNotNull {
            it.toLongOrNull()
        }
    }

    companion object {
        private const val CATEGORIES_SEPARATOR = ", "
    }
}