package com.nurgazy_bolushbekov.product_informer.search_product_info

import com.nurgazy_bolushbekov.product_informer.api_1C.ApiProviderManager
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class SearchProductInfoRepository @Inject constructor(
    private val apiProviderManager: ApiProviderManager
) {

    suspend fun refreshProduct(barcode: String, fullSpecifications: Boolean?): Flow<ResultFetchData<ProductResponse>> = flow {
        try {
            val apiService = apiProviderManager.apiService.filterNotNull().first()
            val response: Response<ResponseBody> = withContext(Dispatchers.IO){
                apiService.info(barcode, fullSpecifications)
            }
            if (response.isSuccessful){
                val responseString = response.body()!!.string()
                val jsonObj = Json { ignoreUnknownKeys = true }
                val jsonString = jsonObj.parseToJsonElement(responseString)

                val productFound: Boolean = jsonString.jsonObject["Результат"].toString().toBoolean()
                if (!productFound){
                    emit(ResultFetchData.Error(Exception("Товар не найден")))
                }

                val productResponse: ProductResponse = jsonObj.decodeFromString(jsonString.jsonObject["Номенклатура"].toString())
                emit(ResultFetchData.Success(productResponse))
            }else{
                emit(ResultFetchData.Error(Exception("Ошибка. Код: ${response.code()}. ${response.message()}")))
            }
        }
        catch (e: Exception){
            emit(ResultFetchData.Error(Exception("Ошибка при получении данных: ${e.message}")))
        }
        emit(ResultFetchData.Loading)
    }
}