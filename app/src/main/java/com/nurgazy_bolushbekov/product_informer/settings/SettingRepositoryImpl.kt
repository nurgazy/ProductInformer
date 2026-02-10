package com.nurgazy_bolushbekov.product_informer.settings

import com.nurgazy_bolushbekov.product_informer.api_1C.ApiProviderManager
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SettingRepositoryImpl @Inject constructor(
    private val apiProviderManager: ApiProviderManager
) {

    suspend fun ping(userName: String, pass: String, url: String): ResultFetchData<String> {
        return withContext(Dispatchers.IO){
            try {
                val apiService = apiProviderManager.create(userName, pass, url)
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
}