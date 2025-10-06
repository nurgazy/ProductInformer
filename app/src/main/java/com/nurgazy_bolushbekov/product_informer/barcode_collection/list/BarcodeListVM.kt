package com.nurgazy_bolushbekov.product_informer.barcode_collection.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc
import com.nurgazy_bolushbekov.product_informer.barcode_collection.repo.BarcodeDocRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeListVM @Inject constructor(
    private val barcodeDocRepository: BarcodeDocRepository
): ViewModel() {

    private val _barcodeDocList = MutableStateFlow<List<BarcodeDoc>>(emptyList())
    val barcodeDocList: StateFlow<List<BarcodeDoc>> = _barcodeDocList.asStateFlow()

    fun refreshBarcodeDocList(){
        viewModelScope.launch {
            barcodeDocRepository.getBarcodeDocs().collect{
                _barcodeDocList.value = it
            }
        }
    }

    fun onDeleteDoc(barcodeDoc: BarcodeDoc){
        viewModelScope.launch {
            barcodeDocRepository.deleteBarcodeDoc(barcodeDoc)
        }
    }
}