package com.nurgazy_bolushbekov.product_informer.api_1C

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

interface AuthApi {
    @GET("login")
    suspend fun check(@Body request: LoginRequest): Response<Unit>
}