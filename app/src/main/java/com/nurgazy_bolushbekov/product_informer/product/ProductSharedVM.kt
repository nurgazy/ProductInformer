package com.nurgazy_bolushbekov.product_informer.product

import androidx.lifecycle.ViewModel
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductSharedVM @Inject constructor(): ViewModel(){

    private val _productData = MutableStateFlow<ProductResponse?>(null)
    val productData: StateFlow<ProductResponse?> = _productData.asStateFlow()

    fun onSetProductData(productData: ProductResponse?) {
        _productData.value = productData
    }

}