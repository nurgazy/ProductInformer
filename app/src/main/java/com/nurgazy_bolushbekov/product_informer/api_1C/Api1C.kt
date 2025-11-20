package com.nurgazy_bolushbekov.product_informer.api_1C

import com.nurgazy_bolushbekov.product_informer.barcode_collection.data_serialization.BarcodeDocumentUpload
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api1C {
    @GET("hs/ProductInformation/Ping/")
    suspend fun ping(): Response<ResponseBody>

    @GET("hs/ProductInformation/Info/")
    suspend fun info(
        @Query("barcode") barcode: String,
        @Query("full") fullSpecifications: Boolean? = null
    ): Response<ResponseBody>

    @POST("hs/ProductInformation/Document")
    suspend fun uploadDocument(
        @Body documentRequest: BarcodeDocumentUpload
    ): Response<ResponseBody>
}