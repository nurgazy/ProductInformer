package com.nurgazy_bolushbekov.product_informer.api_1C

import retrofit2.Response
import retrofit2.http.GET

interface Api1C {
    @GET("ping")
    suspend fun ping(): Response<ResponseData>
}