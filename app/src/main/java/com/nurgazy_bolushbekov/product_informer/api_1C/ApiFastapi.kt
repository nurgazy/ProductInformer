package com.nurgazy_bolushbekov.product_informer.api_1C

import ProductJson
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiFastapi {
    @GET("get_goods/{fileName}")
    suspend fun getProducts(
        @Path("fileName") fileName: String
    ): List<ProductJson>
}