package com.nurgazy_bolushbekov.product_informer.product_information

import com.nurgazy_bolushbekov.product_informer.api_1C.Api1C
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import com.nurgazy_bolushbekov.product_informer.api_1C.RetrofitClient
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response

class ProductInformationRepositoryImp(username:String, password:String, baseUrl:String): ApiRepository {

    private var apiService = RetrofitClient.create(username, password, baseUrl)

    override suspend fun ping(): String {
        throw NotImplementedError("Not yet implemented")
    }

    override suspend fun info(barcode: String): Flow<ResultFetchData<Product>> = flow{
        emit(ResultFetchData.Loading)

        val response: Response<ResponseBody> = withContext(Dispatchers.IO){
            apiService.info(barcode)
        }

        if (response.isSuccessful){
            val responseString = response.body()!!.string()
            val _jsonObj = Json { ignoreUnknownKeys = true }
            val _jsonString = _jsonObj.parseToJsonElement(responseString)

            val productFound: Boolean = _jsonString.jsonObject["Результат"].toString().toBoolean()
            if (!productFound){
                emit(ResultFetchData.Error(Exception("Товар не найден")))
                return@flow
            }
            val product: Product = _jsonObj.decodeFromString(_jsonString.jsonObject["Номенклатура"].toString())
            product.productSpecifications = _jsonObj.decodeFromString(_jsonString.jsonObject["Характеристики"].toString())

            emit(ResultFetchData.Success(product))
        }
    }.catch { e ->
        emit(ResultFetchData.Error(e))
    }
}