package com.nurgazy_bolushbekov.product_informer.price_checker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository

class PriceCheckerViewModelFactory(private val apiRepository: ApiRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PriceCheckerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PriceCheckerViewModel(apiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}