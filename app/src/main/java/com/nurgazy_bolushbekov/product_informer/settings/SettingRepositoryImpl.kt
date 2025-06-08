package com.nurgazy_bolushbekov.product_informer.settings

import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import com.nurgazy_bolushbekov.product_informer.api_1C.RetrofitClient
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext


class SettingRepositoryImpl(username: String, password: String, baseUrl: String) :
    ApiRepository {

    private var apiService = RetrofitClient.create(username, password, baseUrl)

    override suspend fun ping(): ResultFetchData<String> {
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.ping()
                if (response.isSuccessful){
                    ResultFetchData.Success(response.body()!!.string())
                }else{
                    ResultFetchData.Error(Exception("Ошибка при получении данных: ${response.code()}. ${response.message()}"))
                }
            }catch (e: Exception) {
                ResultFetchData.Error(Exception("Ошибка при получении данных: ${e.message}"))
            }
        }
    }

    override suspend fun info(barcode: String): Flow<ResultFetchData<Product>> {
        return emptyFlow()
    }
}