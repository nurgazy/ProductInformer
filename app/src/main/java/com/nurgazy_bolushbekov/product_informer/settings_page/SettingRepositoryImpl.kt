package com.nurgazy_bolushbekov.product_informer.settings_page

import com.nurgazy_bolushbekov.product_informer.api_1C.RetrofitClient
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response


class SettingRepositoryImpl(username: String, password: String, baseUrl: String) :
    ApiRepository {

    private var apiService = RetrofitClient.create(username, password, baseUrl)

    override suspend fun ping(): String = withContext(Dispatchers.IO){
        val response: Response<ResponseBody> = apiService.ping()
        if (response.isSuccessful){
            response.body()!!.string()
        }else{
            "Ошибка при получении данных: ${response.code()}. ${response.message()}"
        }
    }.toString()

    override suspend fun info(barcode: String): String {
        throw NotImplementedError("Not yet implemented")
    }

}