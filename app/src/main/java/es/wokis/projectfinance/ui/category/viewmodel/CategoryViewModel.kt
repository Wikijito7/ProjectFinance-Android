package es.wokis.projectfinance.ui.category.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_CATEGORY_ID
import es.wokis.projectfinance.data.domain.category.CheckInvoicesWithoutCategoryUseCase
import es.wokis.projectfinance.data.domain.category.DeleteCategoryUseCase
import es.wokis.projectfinance.data.domain.category.GetCategoriesUseCase
import es.wokis.projectfinance.data.domain.category.InsertRemovedCategoryUseCase
import es.wokis.projectfinance.ui.category.mapper.toBO
import es.wokis.projectfinance.ui.category.mapper.toVO
import es.wokis.projectfinance.ui.category.vo.CategoryVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val insertRemovedCategoryUseCase: InsertRemovedCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val checkInvoicesWithoutCategoryUseCase: CheckInvoicesWithoutCategoryUseCase
) : ViewModel() {
    // region private livedata
    private val categoriesLiveData: MutableLiveData<List<CategoryVO>> = MutableLiveData()
    private val categoryDeletedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val editCategoryLiveData: MutableLiveData<Long> = MutableLiveData()
    // endregion
    var selectedCategoryId = 0L
    var categorySelected: CategoryVO? = null
        private set

    private var categories: List<CategoryVO> = emptyList()

    // region public live data
    fun getCategoriesLiveData() = categoriesLiveData as LiveData<List<CategoryVO>>
    fun getCategoryDeletedLiveData() = categoryDeletedLiveData as LiveData<Boolean>
    fun getEditCategoryLiveData() = editCategoryLiveData as LiveData<Long>
    // endregion

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            getCategoriesUseCase().collect {
                categories = it.toVO(selectedCategoryId)
                categoriesLiveData.postValue(categories)
            }
        }
    }

    fun onCategoryClick(category: CategoryVO) {
        categorySelected = category
        selectedCategoryId = category.id
        categories = categories.map {
            it.copy(selected = it.id == category.id)
        }
        categoriesLiveData.postValue(categories)
    }

    fun deleteCategory(position: Int) {
        val category = categories[position]
        viewModelScope.launch(Dispatchers.IO) {
            val categoryDeleted = deleteCategoryUseCase(category.toBO()) > 0
            if (category.id == selectedCategoryId) {
                selectedCategoryId = DEFAULT_CATEGORY_ID
                getCategories()
            }
            categoryDeletedLiveData.postValue(categoryDeleted)
        }
    }

    fun editCategory(position: Int) {
        val category = categories[position]
        editCategoryLiveData.postValue(category.id)
    }

    fun reinsertCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            insertRemovedCategoryUseCase()
        }
    }

    fun checkInvoicesWithoutCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            checkInvoicesWithoutCategoryUseCase()
        }
    }
}