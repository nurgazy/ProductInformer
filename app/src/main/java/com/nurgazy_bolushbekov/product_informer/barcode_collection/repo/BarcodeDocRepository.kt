package com.nurgazy_bolushbekov.product_informer.barcode_collection.repo

import com.nurgazy_bolushbekov.product_informer.api_1C.ApiProviderManager
import com.nurgazy_bolushbekov.product_informer.barcode_collection.dao.BarcodeDocDao
import com.nurgazy_bolushbekov.product_informer.barcode_collection.data_serialization.BarcodeDocumentUpload
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDocDetail
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeWithDetails
import com.nurgazy_bolushbekov.product_informer.utils.BarcodeStatus
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class BarcodeDocRepository @Inject constructor(
    private val apiProviderManager: ApiProviderManager,
    private val barcodeDocDao: BarcodeDocDao
) {
    suspend fun saveBarcodeDoc(barcodeDoc: BarcodeDoc): Long {
        return barcodeDocDao.insertBarcode(barcodeDoc)
    }

    suspend fun deleteBarcodeDoc(barcodeDoc: BarcodeDoc){
        return barcodeDocDao.deleteBarcodeDoc(barcodeDoc)
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

    suspend fun uploadDocumentTo1C(documentUpload: BarcodeDocumentUpload): ResultFetchData<String> {
        return try {
            val apiService = apiProviderManager.apiService.filterNotNull().first()
            val response: Response<ResponseBody> = apiService.uploadDocument(documentUpload)

            if (response.isSuccessful) {
                return ResultFetchData.Success("Ok")
            } else {
                val errorBody = response.errorBody()?.string() ?: "Неизвестная ошибка сервера."
                ResultFetchData.Error(Exception("Ошибка API 1С: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            ResultFetchData.Error(Exception("Сетевая ошибка при выгрузке: ${e.message}"))
        }
    }

    suspend fun updateBarcodeDocStatus(doc: BarcodeDoc, newStatus: BarcodeStatus) {
        val updatedDoc = doc.copy(status = newStatus)
        barcodeDocDao.updateBarcode(updatedDoc)
    }
}