package com.nurgazy_bolushbekov.product_informer.product.product_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.product.entity.SpecificationWithProduct
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
class ProductSpecificationDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val imageRepository: ImageRepositoryImpl,
): ViewModel() {

    private val _specWithProduct = MutableStateFlow<SpecificationWithProduct?>(null)
    val specWithProduct: StateFlow<SpecificationWithProduct?> = _specWithProduct.asStateFlow()

    fun getProductFromDB(productId: String){
        getSpecificationWithProduct(productId)
    }

    fun deleteImageFromCache(){
       viewModelScope.launch(Dispatchers.IO) {
           val product = _specWithProduct.value!!.product
           if (product.savedImagePath != null)
               imageRepository.deleteImageFromCache(product.savedImagePath)
       }
    }

    private fun getSpecificationWithProduct(productId: String){
        viewModelScope.launch {
            productRepository.getSpecificationWithProduct(productId).collect{
                _specWithProduct.value = it
            }
        }
    }
}