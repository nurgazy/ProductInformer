package com.nurgazy_bolushbekov.product_informer.api_1C

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface Api1C {
    @GET
    suspend fun ping(@Url url: String): Response<ResponseBody>
}