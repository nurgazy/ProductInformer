package com.nurgazy_bolushbekov.product_informer.api_1C

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface Api1C {
    @GET("hs/BarcodeInfo/Ping/")
    suspend fun ping(): Response<ResponseBody>
}