package com.nurgazy_bolushbekov.product_informer.product

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProductSharedViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductSharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductSharedViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}