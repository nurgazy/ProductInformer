package com.nurgazy_bolushbekov.product_informer.search_product_info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.application.DataStoreRepository
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchProductInfoViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val searchProductInfoRepository: SearchProductInfoRepository
): ViewModel() {

    val serverUrl: StateFlow<String> = dataStoreRepository.serverUrl.asStateFlow()
    private val userName: StateFlow<String> = dataStoreRepository.userName.asStateFlow()
    private val password: StateFlow<String> = dataStoreRepository.password.asStateFlow()
    private val baseUrl: StateFlow<String> = dataStoreRepository.baseUrl.asStateFlow()
    private val isFullSpecifications: StateFlow<Boolean> = dataStoreRepository.isFullSpecifications.asStateFlow()

    private val _barcode = MutableStateFlow("")
    val barcode: StateFlow<String> = _barcode.asStateFlow()

    private val _productResponse = MutableStateFlow<ResultFetchData<ProductResponse>?>(null)
    val productResponse: StateFlow<ResultFetchData<ProductResponse>?> = _productResponse.asStateFlow()

    private val _navigateDetailScreen = MutableStateFlow(false)
    val navigateDetailScreen: StateFlow<Boolean> = _navigateDetailScreen.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _alertText = MutableStateFlow("")
    val alertText: StateFlow<String> = _alertText.asStateFlow()

    fun onChangeBarcode(newBarcode: String) {
        _barcode.value = newBarcode
    }

    private fun setShowAlertDialog(){
        _showDialog.value = true
    }

    fun resetShowAlertDialog(){
        _showDialog.value = false
    }

    private fun setNavigationDetailScreen(){
        _navigateDetailScreen.value = true
    }

    fun resetNavigationDetailScreen(){
        _navigateDetailScreen.value = false
    }

    fun getInfo() {
        Log.d("ProductInformer", "SearchProductInfoViewModel.getInfo barcode: ${_barcode.value}," +
                " isAllSpecifications: ${isFullSpecifications.value}")

        viewModelScope.launch {
            _productResponse.value = ResultFetchData.Loading
            if (_barcode.value.isEmpty()) {
                _productResponse.value = ResultFetchData.Error(Exception("Штрихкод не может быть пустым"))
                _alertText.value = "Штрихкод не может быть пустым"
                setShowAlertDialog()
                resetNavigationDetailScreen()
                return@launch
            }
            searchProductInfoRepository.refreshProduct(_barcode.value, isFullSpecifications.value).collectLatest { result ->
                when (result) {
                    is ResultFetchData.Success -> {
                        try {
                            _productResponse.value = result
                            setNavigationDetailScreen()
                        }catch (e: Exception){
                            _productResponse.value = ResultFetchData.Error(e)
                            _alertText.value = e.message.toString()
                            setShowAlertDialog()
                            resetNavigationDetailScreen()
                        }
                    }
                    is ResultFetchData.Error -> {
                        _productResponse.value = result
                        _alertText.value = result.exception.message.toString()
                        setShowAlertDialog()
                        resetNavigationDetailScreen()
                    }

                    ResultFetchData.Loading -> {
                        _productResponse.value = result
                        resetNavigationDetailScreen()
                    }
                }

            }
        }
    }
}