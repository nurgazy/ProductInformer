package com.nurgazy_bolushbekov.product_informer.product

import androidx.lifecycle.ViewModel
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductSharedViewModel: ViewModel() {
    private val _currentProduct = MutableStateFlow<Product?>(null)
    val currentProduct: StateFlow<Product?> = _currentProduct.asStateFlow()

    fun setProduct(product: Product) {
        _currentProduct.value = product
    }

    fun clearProduct() {
        _currentProduct.value = null
    }
}