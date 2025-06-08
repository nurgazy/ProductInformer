package com.nurgazy_bolushbekov.product_informer.product_information

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository

class ProductInformationViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductInformationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductInformationViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}