package com.nurgazy_bolushbekov.product_informer.price_checker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurgazy_bolushbekov.product_informer.settings_page.SettingViewModel

class PriceCheckerViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PriceCheckerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PriceCheckerViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}