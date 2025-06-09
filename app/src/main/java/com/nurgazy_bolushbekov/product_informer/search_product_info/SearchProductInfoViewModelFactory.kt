package com.nurgazy_bolushbekov.product_informer.search_product_info

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchProductInfoViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchProductInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchProductInfoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}