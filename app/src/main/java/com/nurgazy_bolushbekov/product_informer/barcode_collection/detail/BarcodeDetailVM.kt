package com.nurgazy_bolushbekov.product_informer.barcode_collection.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDocDetail
import com.nurgazy_bolushbekov.product_informer.barcode_collection.repo.BarcodeDocRepository
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import com.nurgazy_bolushbekov.product_informer.product.repo.ProductRepository
import com.nurgazy_bolushbekov.product_informer.utils.BarcodeStatus
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeDetailVM @Inject constructor(
    private val barcodeDocRepository: BarcodeDocRepository,
    private val productRepository: ProductRepository
): ViewModel() {

    private val _curBarcodeDoc = MutableStateFlow<BarcodeDoc?>(null)
    val curBarcodeDoc: StateFlow<BarcodeDoc?> = _curBarcodeDoc.asStateFlow()

    private val _barcodeList = MutableStateFlow<List<BarcodeDocDetail>>(emptyList())
    val barcodeList: StateFlow<List<BarcodeDocDetail>> = _barcodeList.asStateFlow()

    private val _productResponse = MutableStateFlow<ResultFetchData<ProductResponse>?>(null)

    private val _curBarcodeData = MutableStateFlow<BarcodeDocDetail?>(null)
    val curBarcodeData: StateFlow<BarcodeDocDetail?> = _curBarcodeData.asStateFlow()

    fun refreshProduct(barcode: String){
        viewModelScope.launch {
            if (barcode.isEmpty()) {
                Log.d("ProductInformer", "Штрихкод не может быть пустым")
                return@launch
            }

            _productResponse.value = ResultFetchData.Loading
            when(val result = productRepository.refreshProduct(barcode, false)){
                is ResultFetchData.Success -> {
                    _productResponse.value = result

                    _curBarcodeData.value = getBarcodeDocDetail(result.data)
                    if (_curBarcodeData.value != null)
                        addToBarcodeList(_curBarcodeData.value!!)
                }
                is ResultFetchData.Error -> {
                    _productResponse.value = result
                }
                ResultFetchData.Loading ->{
                    Log.d("ProductInformer", "Loading data")
                }
            }
        }
    }

    private fun getBarcodeDocDetail(productData: ProductResponse?): BarcodeDocDetail?{
        if (productData == null) return null

        val productSpecName = if (productData.productSpecificResponses == null) "" else productData.productSpecificResponses!![0].name
        val productSpecUuid1C = if (productData.productSpecificResponses == null) "" else productData.productSpecificResponses!![0].uuid1C

        val barcodeDocDetail = BarcodeDocDetail(
            barcode = productData.barcode,
            productName = productData.name,
            productUuid1C = productData.uuid1C,
            productSpecName = productSpecName,
            productSpecUuid1C = productSpecUuid1C
        )
        return barcodeDocDetail
    }

    fun setBarcodeDoc(barcodeDocId: Long){
        if (barcodeDocId == 0.toLong()) return
        viewModelScope.launch {
            barcodeDocRepository.getBarcodeDocWithDetailsById(barcodeDocId).collect{
                _curBarcodeDoc.value = it.barcodeDoc
                _barcodeList.value = it.details
            }
        }
    }

    fun saveCurBarcodeDoc(){
        if(_curBarcodeDoc.value == null){
            val newBarcodeDoc = BarcodeDoc(
                status = BarcodeStatus.ACTIVE,
            )

            viewModelScope.launch {
                barcodeDocRepository.saveBarcodeDocWithDetails(newBarcodeDoc, _barcodeList.value)
            }
        }else{
            viewModelScope.launch {
                barcodeDocRepository.saveBarcodeDocWithDetails(_curBarcodeDoc.value!!, _barcodeList.value)
            }

        }
    }

    private fun addToBarcodeList(item: BarcodeDocDetail){
        val currentBarcodeList = _barcodeList.value
        val updatedBarcodeList = currentBarcodeList.plus(item)
        _barcodeList.value = updatedBarcodeList
    }

    fun removeFromBarcodeList(item: BarcodeDocDetail) {
        val updatedList = _barcodeList.value.toMutableList()
        updatedList.remove(item)
        _barcodeList.value = updatedList
    }
}