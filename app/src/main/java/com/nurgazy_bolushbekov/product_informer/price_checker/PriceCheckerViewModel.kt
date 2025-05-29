package com.nurgazy_bolushbekov.product_informer.price_checker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PriceCheckerViewModel(private val apiRepository: ApiRepository): ViewModel() {
    private val _barcode = MutableStateFlow("")
    val barcode: StateFlow<String> = _barcode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _infoResponse = MutableStateFlow("")
    val infoResponse: StateFlow<String> = _infoResponse.asStateFlow()

    fun onChangeBarcode(newBarcode: String) {
        _barcode.value = newBarcode
    }

    fun getInfo() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _infoResponse.value = apiRepository.info(_barcode.value)
                Log.d("ProductInformer", _infoResponse.value)
            } catch (e: Exception){
                _infoResponse.value = "Сообщение об ошибке: ${e.message.toString()}"
                Log.d("ProductInformer", _infoResponse.value)
            }finally {
                _isLoading.value = false
            }
        }
    }
}