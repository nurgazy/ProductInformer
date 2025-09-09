package com.nurgazy_bolushbekov.product_informer.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import com.nurgazy_bolushbekov.product_informer.product.image.ImageRepository
import com.nurgazy_bolushbekov.product_informer.product.image.ImageRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductSharedViewModel @Inject constructor(application: Application): AndroidViewModel(application) {

    private val imageRepository: ImageRepository = ImageRepositoryImpl(application)

    private val _currentProductResponse = MutableStateFlow<ProductResponse?>(null)
    val currentProductResponse: StateFlow<ProductResponse?> = _currentProductResponse.asStateFlow()

    fun setProduct(productResponse: ProductResponse) {
        _currentProductResponse.value = productResponse
    }

    fun deleteImage(){
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.deleteImageFromCache(_currentProductResponse.value!!.savedImagePath!!)
        }
    }
}