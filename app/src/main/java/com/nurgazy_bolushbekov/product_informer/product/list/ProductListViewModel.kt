package com.nurgazy_bolushbekov.product_informer.product.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiProviderManager
import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductSpecification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val apiProviderManager: ApiProviderManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val products: StateFlow<List<Product>> = productDao.getAllProducts()
        .combine(_searchQuery) { list, query ->
            if (query.isBlank()) {
                list
            } else {
                list.filter { product ->
                    product.productName.contains(query, ignoreCase = true) ||
                            product.barcode.contains(query)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun scanQrCode(qrCode: String) {
        downloadJson("$qrCode.json")
    }

    private fun downloadJson(fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                _isLoading.value = true
                val apiService = apiProviderManager.fastApiService.value

                if (apiService == null) {
                    Log.e("ProductInformer", "Api не инициализирован")
                    return@launch
                }

                val items = apiService.getProducts(fileName)

                productDao.clearAllProducts()

                items.forEach { item ->
                    val product = Product(
                        productName = item.details.name,
                        barcode = item.details.barcode,
                        productUuid1C = item.details.guid,
                        article = "", manufacturer = "", brand = "", productCategory = "", savedImagePath = null
                    )

                    val specs = item.details.specifications.map { spec ->
                        ProductSpecification(
                            name = spec.name,
                            uuid1C = spec.guid,
                            specificationBarcode = spec.barcode,
                            productId = 0
                        )
                    }

                    productDao.insertProductWithSpecs(product, specs)

                }
                Log.d("ProductInformer", "Успешно загружено ${items.size} товаров")
            } catch (e: Exception) {
                Log.e("ProductInformer", "Ошибка загрузки: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}