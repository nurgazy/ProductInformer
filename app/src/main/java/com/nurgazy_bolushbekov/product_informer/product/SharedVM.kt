package com.nurgazy_bolushbekov.product_informer.product

import androidx.lifecycle.ViewModel
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedVM @Inject constructor(): ViewModel(){

    private val _productData = MutableStateFlow<ProductResponse?>(null)
    val productData: StateFlow<ProductResponse?> = _productData.asStateFlow()

    private val _previousRoute= MutableStateFlow<String?>(null)
    val previousRoute: StateFlow<String?> = _previousRoute.asStateFlow()

    fun onSetProductData(productData: ProductResponse?) {
        _productData.value = productData
    }

    fun resetProductData() {
        _productData.value = null
    }

    fun setPreviousRoute(route: String?) {
        _previousRoute.value = route
    }

}