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
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductSpecificResponse
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductSpecification
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductWithSpecifications
import com.nurgazy_bolushbekov.product_informer.product.repo.ProductRepository
import com.nurgazy_bolushbekov.product_informer.utils.BarcodeStatus
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val isCyclicScanning: StateFlow<Boolean> = dataStoreRepository.isCyclicScanning.asStateFlow()

    private val _triggerScanner = MutableStateFlow(false)
    val triggerScanner: StateFlow<Boolean> = _triggerScanner.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private val _availableSpecs = MutableStateFlow<List<ProductSpecificResponse>>(emptyList())
    val availableSpecs: StateFlow<List<ProductSpecificResponse>> = _availableSpecs.asStateFlow()

    private val _showSpecSelectionDialog = MutableStateFlow(false)
    val showSpecSelectionDialog: StateFlow<Boolean> = _showSpecSelectionDialog.asStateFlow()

    private var pendingProductData: ProductResponse? = null
    private var isManualInputProcess: Boolean = false

    fun resetScannerTrigger() { _triggerScanner.value = false }

    val filteredBarcodeList: StateFlow<List<BarcodeDocDetail>> =
        _searchQuery
            .combine(_barcodeList) { query, list ->
                if (query.isBlank()) {
                    list
                } else {
                    list.filter { item ->
                        item.barcode.contains(query, ignoreCase = true) ||
                                item.productName.contains(query, ignoreCase = true)
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun refreshProduct(barcode: String, isManual: Boolean = false){
        isManualInputProcess = isManual

        viewModelScope.launch {
            if (barcode.isEmpty()) {
                Log.d("ProductInformer", "Штрихкод не может быть пустым")
                return@launch
            }

            _productResponse.value = ResultFetchData.Loading
            val localProduct = productRepository.getProductFromDb(barcode)

            if (localProduct != null) {
                // Если нашли в БД, обрабатываем локальные данные
                val productResponse = mapToResponse(localProduct)
                handleProductSuccess(productResponse, isManual)
            } else {
                // 2. Если в БД нет, идем в сеть
                fetchProductFromNetwork(barcode, isManual)
            }
        }
    }

    fun setBarcodeDoc(barcodeDocId: Long){
        if (barcodeDocId == 0.toLong()) return
        viewModelScope.launch {
            barcodeDocRepository.getBarcodeDocWithDetailsById(barcodeDocId).collect{
                _curBarcodeDoc.value = it.barcodeDoc
                _barcodeList.value = it.details
                _comment.value = _curBarcodeDoc.value?.comment!!
            }
        }
    }

    fun saveCurBarcodeDoc(){
        if(_curBarcodeDoc.value == null){
            val newBarcodeDoc = BarcodeDoc(
                status = BarcodeStatus.ACTIVE,
                comment = _comment.value
            )

            viewModelScope.launch {
                barcodeDocRepository.saveBarcodeDocWithDetails(newBarcodeDoc, _barcodeList.value)
            }
        }else{
            _curBarcodeDoc.value = _curBarcodeDoc.value!!.copy(comment = _comment.value)
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
            val updateItem = existItem.copy(quantity = existItem.quantity + quantity)
            curBarcodeList[existIndex] = updateItem
            _curBarcodeData.value = updateItem
        }else{
            val itemToAdd = newItem.copy(quantity = quantity)
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

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault()) // Используем часовой пояс системы

        val dateString = formatter.format(Instant.ofEpochMilli(doc.creationTimestamp))

        val dataFor1C = BarcodeDocumentUpload(
            internalId = doc.barcodeDocId,
            uuid1C = doc.uuid1C,
            userName = _userName.value,
            docDate = dateString,
            comment = doc.comment?: "",
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

    fun onCommentChange(newComment: String) {
        _comment.value = newComment
    }

    fun onSpecSelected(spec: ProductSpecificResponse) {
        _showSpecSelectionDialog.value = false
        processProductWithSpec(pendingProductData, spec, isManualInputProcess)
        pendingProductData = null
        isManualInputProcess = false
    }

    fun dismissSpecSelection() {
        _showSpecSelectionDialog.value = false
        pendingProductData = null
    }

    private fun processProductWithSpec(
        productData: ProductResponse?,
        spec: ProductSpecificResponse?,
        isManual: Boolean
    ) {
        if (productData == null) return

        val detail = BarcodeDocDetail(
            barcode = productData.barcode,
            productName = productData.name,
            productUuid1C = productData.uuid1C,
            productSpecName = spec?.name ?: "",
            productSpecUuid1C = spec?.uuid1C ?: ""
        )

        _curBarcodeData.value = detail

        if (isCyclicScanning.value && !isManual) {
            addToBarcodeList(quantity = 1)
            _triggerScanner.value = true
        } else {
            setShowQuantityInputDialog()
        }
    }

    private suspend fun saveProductLocally(productResponse: ProductResponse) {

        val product = Product(
            productName = productResponse.name,
            barcode = productResponse.barcode,
            article = productResponse.article,
            manufacturer = productResponse.manufacturer,
            brand = productResponse.brand,
            productCategory = productResponse.productCategory,
            productUuid1C = productResponse.uuid1C,
            savedImagePath = null
        )

        val specifications = productResponse.productSpecificResponses?.map { specRes ->
            ProductSpecification(
                name = specRes.name,
                uuid1C = specRes.uuid1C,
                specificationBarcode = specRes.barcode
            )
        } ?: emptyList()

        productRepository.saveProductWithSpecs(product, specifications)
    }

    private suspend fun fetchProductFromNetwork(barcode: String, isManual: Boolean) {
        when (val result = productRepository.refreshProduct(barcode, false)) {
            is ResultFetchData.Success -> {
                val productData = result.data
                saveProductLocally(productData) // Сохраняем для будущего использования
                handleProductSuccess(productData, isManual)
                _productResponse.value = result
            }
            is ResultFetchData.Error -> {
                _productResponse.value = result
            }
            else -> {}
        }
    }

    private fun handleProductSuccess(productData: ProductResponse, isManual: Boolean) {
        val specs = productData.productSpecificResponses ?: emptyList()
        if (specs.size > 1) {
            pendingProductData = productData
            _availableSpecs.value = specs
            _showSpecSelectionDialog.value = true
        } else {
            processProductWithSpec(productData, specs.getOrNull(0), isManual)
        }
    }

    private fun mapToResponse(local: ProductWithSpecifications): ProductResponse {
        return ProductResponse(
            name = local.product.productName,
            barcode = local.product.barcode,
            article = local.product.article,
            manufacturer = local.product.manufacturer,
            brand = local.product.brand,
            productCategory = local.product.productCategory,
            uuid1C = local.product.productUuid1C,
            productSpecificResponses = local.specifications.map { spec ->
                ProductSpecificResponse(
                    name = spec.name,
                    uuid1C = spec.uuid1C,
                    barcode = spec.specificationBarcode
                )
            }
        )
    }
}