package com.nurgazy_bolushbekov.product_informer.price_checker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PriceCheckerViewModel(private val apiRepository: ApiRepository): ViewModel() {
    private val _barcode = MutableStateFlow("")
    val barcode: StateFlow<String> = _barcode.asStateFlow()

    private val _product = MutableStateFlow<ResultFetchData<Product>?>(null)
    val product: StateFlow<ResultFetchData<Product>?> = _product.asStateFlow()

    private val _navigateDetailScreen = MutableStateFlow(false)
    val navigateDetailScreen: StateFlow<Boolean> = _navigateDetailScreen.asStateFlow()

    fun onChangeBarcode(newBarcode: String) {
        _barcode.value = newBarcode
    }

    fun resetNavigationDetailScreen(){
        _navigateDetailScreen.value = false
    }

    fun getInfo() {
        viewModelScope.launch {
            _product.value = ResultFetchData.Loading
            if (_barcode.value.isEmpty()) {
                _product.value = ResultFetchData.Error(Exception("Штрихкод не может быть пустым"))
                _navigateDetailScreen.value = false
                return@launch
            }
            apiRepository.info(_barcode.value).collectLatest { result ->
                when (result) {
                    is ResultFetchData.Success -> {
                        try {
                            _product.value = result
                            _navigateDetailScreen.value = true
                        }catch (e: Exception){
                            _product.value = ResultFetchData.Error(e)
                            _navigateDetailScreen.value = false
                        }
                    }
                    is ResultFetchData.Error -> {
                        _product.value = result
                        _navigateDetailScreen.value = false
                    }

                    ResultFetchData.Loading -> {
                        _product.value = result
                        _navigateDetailScreen.value = false
                    }
                }

            }
        }
    }
}