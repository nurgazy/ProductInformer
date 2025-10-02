package com.nurgazy_bolushbekov.product_informer.barcode_collection.repo

import com.nurgazy_bolushbekov.product_informer.barcode_collection.dao.BarcodeDocDao
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDocDetail
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeWithDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BarcodeDocRepository @Inject constructor(
    private val barcodeDocDao: BarcodeDocDao
) {
    suspend fun saveBarcodeDoc(barcodeDoc: BarcodeDoc): Long {
        return barcodeDocDao.insertBarcode(barcodeDoc)
    }

    fun getBarcodeDocs(): Flow<List<BarcodeDoc>> {
        return barcodeDocDao.getAllBarcodeDocs()
    }

    fun getBarcodeDocById(barcodeDocId: Long): Flow<BarcodeDoc> {
        return barcodeDocDao.getBarcodeDocById(barcodeDocId)
    }

    fun getBarcodeDocWithDetailsById(barcodeDocId: Long): Flow<BarcodeWithDetails> {
        return barcodeDocDao.getBarcodeDocWithDetailsById(barcodeDocId)
    }

    suspend fun saveBarcodeDocWithDetails(barcodeDoc: BarcodeDoc, barcodeDocDetails: List<BarcodeDocDetail>) {
        return barcodeDocDao.saveBarcodeDoc(barcodeDoc, barcodeDocDetails)
    }
}