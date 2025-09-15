package com.nurgazy_bolushbekov.product_informer.product.product_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.application.DataStoreRepository
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import com.nurgazy_bolushbekov.product_informer.product.image.ImageRepositoryImpl
import com.nurgazy_bolushbekov.product_informer.product.repo.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val productRepository: ProductRepository,
    private val imageRepository: ImageRepositoryImpl,
): ViewModel() {

    private val isFullSpecifications: StateFlow<Boolean> = dataStoreRepository.isFullSpecifications.asStateFlow()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    fun getProductFromDB(productId: String){
        if (isFullSpecifications.value){
            getProductByUuid1C(productId)
        }else{
            getProductByBarcode(productId)
        }
    }

    fun deleteImageFromCache(){
        viewModelScope.launch(Dispatchers.IO) {
            if (product.value!!.savedImagePath != null)
                imageRepository.deleteImageFromCache(product.value!!.savedImagePath!!)
        }
    }

    private fun getProductByUuid1C(productId: String){
        viewModelScope.launch {
            _product.value = productRepository.getProductByUuid1C(productId)
        }
    }

    private fun getProductByBarcode(productId: String){
        viewModelScope.launch {
            _product.value = productRepository.getProductByBarcode(productId)
        }
    }

}