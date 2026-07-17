package com.nurgazy_bolushbekov.product_informer.api_1C

import ProductJson
import com.nurgazy_bolushbekov.product_informer.barcode_collection.data_serialization.BarcodeDocumentUpload
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiFastapi {
    @GET("get_goods/{fileName}")
    suspend fun getProducts(
        @Path("fileName") fileName: String
    ): List<ProductJson>

    @POST("save_basket")
    suspend fun uploadToFastAPI(
        @Body documentRequest: BarcodeDocumentUpload
    ): Response<ResponseBody>
}