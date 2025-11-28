package com.nurgazy_bolushbekov.product_informer.barcode_collection.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.application.DataStoreRepository
import com.nurgazy_bolushbekov.product_informer.barcode_collection.data_serialization.BarcodeDocumentUpload
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDocDetail
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.toBarcodeDocumentItem
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
    private val productRepository: ProductRepository,
    dataStoreRepository: DataStoreRepository
): ViewModel() {

    private val _curBarcodeDoc = MutableStateFlow<BarcodeDoc?>(null)
    val curBarcodeDoc: StateFlow<BarcodeDoc?> = _curBarcodeDoc.asStateFlow()

    private val _barcodeList = MutableStateFlow<List<BarcodeDocDetail>>(emptyList())
    val barcodeList: StateFlow<List<BarcodeDocDetail>> = _barcodeList.asStateFlow()

    private val _productResponse = MutableStateFlow<ResultFetchData<ProductResponse>?>(null)

    private val _curBarcodeData = MutableStateFlow<BarcodeDocDetail?>(null)
    val curBarcodeData: StateFlow<BarcodeDocDetail?> = _curBarcodeData.asStateFlow()

    private val _showQuantityInputDialog = MutableStateFlow<Boolean>(false)
    val showQuantityInputDialog: StateFlow<Boolean> = _showQuantityInputDialog.asStateFlow()

    private val _uploadStatusMessage = MutableStateFlow<String?>(null)
    val uploadStatusMessage: StateFlow<String?> = _uploadStatusMessage.asStateFlow()

    private val _userName = dataStoreRepository.userName.asStateFlow()

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
                    setShowQuantityInputDialog()
                }
                is ResultFetchData.Error -> {
                    _productResponse.value = result
                    resetShowQuantityInputDialog()
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

    fun addToBarcodeList(quantity: Int=1){
        val newItem = _curBarcodeData.value?: return

        val curBarcodeList = _barcodeList.value.toMutableList()
        val existIndex = curBarcodeList.indexOfFirst {
            it.barcode == newItem.barcode &&
            it.productUuid1C == newItem.productUuid1C &&
            it.productSpecUuid1C == newItem.productSpecUuid1C
        }

        if (existIndex != -1){
            val existItem = curBarcodeList[existIndex]
            val updateItem = existItem.copy(quantity = quantity)
            curBarcodeList[existIndex] = updateItem
            _curBarcodeData.value = updateItem
        }else{
            val itemToAdd = _curBarcodeData.value!!.copy(quantity = quantity)
            curBarcodeList.add(itemToAdd)
            _curBarcodeData.value = itemToAdd
        }

        _barcodeList.value = curBarcodeList.toList()
        _curBarcodeData.value = null
    }

    fun removeFromBarcodeList(item: BarcodeDocDetail) {
        val updatedList = _barcodeList.value.toMutableList()
        updatedList.remove(item)
        _barcodeList.value = updatedList
    }

    fun setShowQuantityInputDialog(){
        _showQuantityInputDialog.value = true
    }

    fun resetShowQuantityInputDialog(){
        _showQuantityInputDialog.value = false
        _curBarcodeData.value = null
    }

    fun uploadTo1C() {
        val doc = curBarcodeDoc.value
        val items = barcodeList.value

        if (doc == null || items.isEmpty()) {
            Log.d("ProductInformer", "Документ не найден или список пуст.")
            return
        }

        val documentItemsForUpload = items.map { it.toBarcodeDocumentItem() }

        val dataFor1C = BarcodeDocumentUpload(
            internalId = doc.barcodeDocId,
            uuid1C = doc.uuid1C,
            userName = _userName.value,
            items = documentItemsForUpload
        )

        viewModelScope.launch {

            when (val result = barcodeDocRepository.uploadDocumentTo1C(dataFor1C)) {
                is ResultFetchData.Success -> {
                    barcodeDocRepository.updateBarcodeDocStatus(doc, BarcodeStatus.UPLOADED)
                    _curBarcodeDoc.value = doc.copy(status = BarcodeStatus.UPLOADED)
                    _uploadStatusMessage.value = "Успех: Документ №${doc.barcodeDocId} выгружен!"
                }
                is ResultFetchData.Error -> {
                    _uploadStatusMessage.value = "Ошибка выгрузки: ${result.exception.message}"
                    Log.d("ProductInformer", "Ошибка выгрузки", result.exception)
                }

                ResultFetchData.Loading -> {
                }
            }
        }

    }

    fun clearUploadStatusMessage() {
        _uploadStatusMessage.value = null
    }
}