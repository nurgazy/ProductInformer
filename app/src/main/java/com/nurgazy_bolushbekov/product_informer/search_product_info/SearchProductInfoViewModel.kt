package com.nurgazy_bolushbekov.product_informer.search_product_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.application.DataStoreRepository
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchProductInfoViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val searchProductInfoRepository: SearchProductInfoRepository
): ViewModel() {

    val serverUrl: StateFlow<String> = dataStoreRepository.serverUrl.asStateFlow()
    private val isFullSpecifications: StateFlow<Boolean> = dataStoreRepository.isFullSpecifications.asStateFlow()

    private val _barcode = MutableStateFlow("")
    val barcode: StateFlow<String> = _barcode.asStateFlow()

    private val _uuidProduct = MutableStateFlow<String?>("")

    private val _productId = MutableStateFlow<String>("")
    val productId: StateFlow<String> = _productId.asStateFlow()

    private val _refreshResult = MutableStateFlow<ResultFetchData<String>?>(null)
    val refreshResult: StateFlow<ResultFetchData<String>?> = _refreshResult.asStateFlow()

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

//    fun getInfo() {
//
//        viewModelScope.launch {
//            _productResponse.value = ResultFetchData.Loading
//            if (_barcode.value.isEmpty()) {
//                _productResponse.value = ResultFetchData.Error(Exception("Штрихкод не может быть пустым"))
//                _alertText.value = "Штрихкод не может быть пустым"
//                setShowAlertDialog()
//                resetNavigationDetailScreen()
//                return@launch
//            }
//            searchProductInfoRepository.info(_barcode.value, isFullSpecifications.value).collectLatest { result ->
//                when (result) {
//                    is ResultFetchData.Success -> {
//                        try {
//                            _productResponse.value = result
//                            setNavigationDetailScreen()
//                        }catch (e: Exception){
//                            _productResponse.value = ResultFetchData.Error(e)
//                            _alertText.value = e.message.toString()
//                            setShowAlertDialog()
//                            resetNavigationDetailScreen()
//                        }
//                    }
//                    is ResultFetchData.Error -> {
//                        _productResponse.value = result
//                        _alertText.value = result.exception.message.toString()
//                        setShowAlertDialog()
//                        resetNavigationDetailScreen()
//                    }
//
//                    ResultFetchData.Loading -> {
//                        _productResponse.value = result
//                        resetNavigationDetailScreen()
//                    }
//                }
//
//            }
//        }
//    }

    fun refreshProduct(){
        viewModelScope.launch {
            if (_barcode.value.isEmpty()) {
                _alertText.value = "Штрихкод не может быть пустым"
                setShowAlertDialog()
                resetNavigationDetailScreen()
                return@launch
            }

            _refreshResult.value = ResultFetchData.Loading
            val result = searchProductInfoRepository.refreshProduct(_barcode.value, isFullSpecifications.value)
            when(result){
                ResultFetchData.Loading ->{}
                is ResultFetchData.Error -> {
                    _uuidProduct.value = null
                    _refreshResult.value = result
                    _alertText.value = result.exception.message.toString()
                    setShowAlertDialog()
                    resetNavigationDetailScreen()
                }
                is ResultFetchData.Success -> {
                    _uuidProduct.value = result.data
                    _refreshResult.value = result
                    _productId.value = if (isFullSpecifications.value) _uuidProduct.value?:"" else _barcode.value
                    setNavigationDetailScreen()
                }
            }
        }
    }
}