package com.nurgazy_bolushbekov.product_informer.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
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

    private val _currentProduct = MutableStateFlow<Product?>(null)
    val currentProduct: StateFlow<Product?> = _currentProduct.asStateFlow()

    fun setProduct(product: Product) {
        _currentProduct.value = product
    }

    fun deleteImage(){
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.deleteImageFromCache(_currentProduct.value!!.savedImagePath!!)
        }
    }
}