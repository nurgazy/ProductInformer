package com.nurgazy_bolushbekov.product_informer.product.product_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.application.DataStoreRepository
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductWithSpecificationsAndPrices
import com.nurgazy_bolushbekov.product_informer.product.entity.SpecificationsWithPrices
import com.nurgazy_bolushbekov.product_informer.product.repo.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    private val _productData = MutableStateFlow<ProductWithSpecificationsAndPrices?>(null)
    val productData: StateFlow<ProductWithSpecificationsAndPrices?> = _productData.asStateFlow()

    private val isFullSpecifications: StateFlow<Boolean> = dataStoreRepository.isFullSpecifications.asStateFlow()

    fun getProductFromDB(productId: String){
        if (isFullSpecifications.value)
            getProductWithSpecificationsByUuid1C(productId)
        else
            getSpecificationByBarcodeWithProduct(productId)
    }

    private fun getProductWithSpecificationsByUuid1C(productId: String){
        viewModelScope.launch {
            productRepository.getProductWithSpecificationsAndPricesByUuid1C(productId).collect{
                _productData.value = it
            }
        }
    }

    private fun getSpecificationByBarcodeWithProduct(productId: String){
        viewModelScope.launch {
            productRepository.getProductWithSpecificationsByBarcode(productId).collect{
                val productSpec = it.keys.firstOrNull()
                val specificationsWithPrices = it.values.firstOrNull()
                if (productSpec != null && specificationsWithPrices != null) {
                    _productData.value = ProductWithSpecificationsAndPrices(
                        product = productSpec,
                        specificationsWithPrices = listOf(SpecificationsWithPrices(specificationsWithPrices.specification, specificationsWithPrices.prices))
                    )
                }
            }
        }
    }

}