package com.nurgazy_bolushbekov.product_informer.api_1C

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api1C {
    @GET("hs/ProductInformation/Ping/")
    suspend fun ping(): Response<ResponseBody>

    @GET("hs/ProductInformation/Info/")
    suspend fun info(@Query("barcode") barcode: String): Response<ResponseBody>
}