package com.nurgazy_bolushbekov.product_informer.price_checker

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PriceCheckerViewModel: ViewModel() {
    private val _barcode = MutableStateFlow("")
    val barcode: StateFlow<String> = _barcode.asStateFlow()

    fun changeBarcode(newBarcode: String) {
        _barcode.value = newBarcode
    }
}