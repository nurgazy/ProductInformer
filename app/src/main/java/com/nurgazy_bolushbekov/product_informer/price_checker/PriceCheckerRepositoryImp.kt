package com.nurgazy_bolushbekov.product_informer.price_checker

import com.nurgazy_bolushbekov.product_informer.api_1C.Api1C
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

class PriceCheckerRepositoryImp(private val apiService: Api1C): ApiRepository {
    override suspend fun ping(): String {
        throw NotImplementedError("Not yet implemented")
    }

    override suspend fun info(barcode: String): String = withContext(Dispatchers.IO){
        val response: Response<ResponseBody> = apiService.info(barcode)
        if (response.isSuccessful){
            response.body()!!.string()
        }else{
            "Ошибка при получении данных: ${response.code()}. ${response.message()}"
        }
    }.toString()
}