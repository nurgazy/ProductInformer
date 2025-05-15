package com.nurgazy_bolushbekov.product_informer.settings_page

import com.nurgazy_bolushbekov.product_informer.api_1C.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

class SettingsRepositoryImpl(username: String, password: String, baseUrl: String) : SettingsRepository {

    private var apiService = RetrofitClient.create(username, password, baseUrl)

    override suspend fun ping(): String = withContext(Dispatchers.IO){
        val response: Response<ResponseBody> = apiService.ping()
        if (response.isSuccessful){
            response.body()!!.string()
        }else{
            "Ошибка при получении данных: ${response.code()}. ${response.message()}"
        }
    }.toString()

}