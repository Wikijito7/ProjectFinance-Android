package es.wokis.projectfinance.ui.splash.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.wokis.projectfinance.data.constants.AppConstants
import es.wokis.projectfinance.data.domain.category.CheckCategoriesUseCase
import es.wokis.projectfinance.data.domain.category.CheckInvoicesWithoutCategoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkCategoriesUseCase: CheckCategoriesUseCase,
    private val checkInvoicesWithoutCategoryUseCase: CheckInvoicesWithoutCategoryUseCase,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val navigationPendingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getNavigationPendingLiveData() = navigationPendingLiveData as LiveData<Boolean>

    fun checkCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            checkCategoriesUseCase()
        }
    }

    fun checkInvoicesWithoutCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            checkInvoicesWithoutCategoryUseCase()
        }
    }

    fun checkReactionsWithoutInvoice() {
        viewModelScope.launch(Dispatchers.IO) {
            checkReactionsWithoutInvoice()
        }
    }

    fun setNavigationPending() {
        navigationPendingLiveData.postValue(true)
    }

    fun isBiometricsEnabled() = sharedPreferences.getBoolean(AppConstants.BIOMETRICS_ENABLED, false)

}