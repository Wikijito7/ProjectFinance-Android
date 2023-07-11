package es.wokis.projectfinance.ui.category.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.domain.category.CreateCategoryUseCase
import es.wokis.projectfinance.data.domain.category.GetCategoryByIdUseCase
import es.wokis.projectfinance.data.domain.category.UpdateCategoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val createCategoryUse: CreateCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase
) : ViewModel() {
    // region private livedata
    private val categoriesLiveData: MutableLiveData<CategoryBO> = MutableLiveData()
    private val insertedCategoryLiveData: MutableLiveData<Boolean> = MutableLiveData()
    // endregion

    private var categoryId = NEW_CATEGORY_ID

    // region public live data
    fun getCategoriesLiveData() = categoriesLiveData as LiveData<CategoryBO>
    fun getInsertedCategoryLiveData() = insertedCategoryLiveData as LiveData<Boolean>
    // endregion

    fun getCategoryById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val category = getCategoryByIdUseCase(id)
            categoryId = id
            category?.let {
                categoriesLiveData.postValue(it)
            }
        }
    }

    fun addCategory(categoryName: String, categoryColor: String) {
        val category = CategoryBO(categoryId, categoryName, categoryColor)
        viewModelScope.launch(Dispatchers.IO) {
            val inserted = if (categoryId == NEW_CATEGORY_ID) {
                createCategoryUse(category) > 1L

            } else {
                updateCategoryUseCase(category) > 0
            }
            insertedCategoryLiveData.postValue(inserted)
        }
    }

    companion object {
        private const val NEW_CATEGORY_ID = 0L
    }
}