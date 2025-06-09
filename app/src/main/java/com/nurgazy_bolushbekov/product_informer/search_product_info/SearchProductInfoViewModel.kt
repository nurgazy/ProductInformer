package com.nurgazy_bolushbekov.product_informer.search_product_info

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import com.nurgazy_bolushbekov.product_informer.application.App
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchProductInfoViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var apiRepository: ApiRepository
    private val connectionSettingsPrefRep = (application as App).connectionSettingsPrefRep

    private val userName: StateFlow<String> = connectionSettingsPrefRep.userName.asStateFlow()
    private val password: StateFlow<String> = connectionSettingsPrefRep.password.asStateFlow()
    private val baseUrl = connectionSettingsPrefRep.baseUrl.asStateFlow()

    private val _barcode = MutableStateFlow("")
    val barcode: StateFlow<String> = _barcode.asStateFlow()

    private val _product = MutableStateFlow<ResultFetchData<Product>?>(null)
    val product: StateFlow<ResultFetchData<Product>?> = _product.asStateFlow()

    private val _navigateDetailScreen = MutableStateFlow(false)
    val navigateDetailScreen: StateFlow<Boolean> = _navigateDetailScreen.asStateFlow()

    fun onChangeBarcode(newBarcode: String) {
        _barcode.value = newBarcode
    }

    fun resetNavigationDetailScreen(){
        _navigateDetailScreen.value = false
    }

    fun getInfo() {
        apiRepository = SearchProductInfoRepositoryImp(userName.value, password.value, baseUrl.value)
        viewModelScope.launch {
            _product.value = ResultFetchData.Loading
            if (_barcode.value.isEmpty()) {
                _product.value = ResultFetchData.Error(Exception("Штрихкод не может быть пустым"))
                _navigateDetailScreen.value = false
                return@launch
            }
            apiRepository.info(_barcode.value).collectLatest { result ->
                when (result) {
                    is ResultFetchData.Success -> {
                        try {
                            _product.value = result
                            _navigateDetailScreen.value = true
                        }catch (e: Exception){
                            _product.value = ResultFetchData.Error(e)
                            _navigateDetailScreen.value = false
                        }
                    }
                    is ResultFetchData.Error -> {
                        _product.value = result
                        _navigateDetailScreen.value = false
                    }

                    ResultFetchData.Loading -> {
                        _product.value = result
                        _navigateDetailScreen.value = false
                    }
                }

            }
        }
    }
}