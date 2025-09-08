package com.nurgazy_bolushbekov.product_informer.settings

import com.nurgazy_bolushbekov.product_informer.api_1C.ApiProviderManager
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SettingRepositoryImpl @Inject constructor(
    private val apiProviderManager: ApiProviderManager
) {

    suspend fun ping(): ResultFetchData<String> {
        return withContext(Dispatchers.IO){
            try {
                val apiService = apiProviderManager.apiService.filterNotNull().first()
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