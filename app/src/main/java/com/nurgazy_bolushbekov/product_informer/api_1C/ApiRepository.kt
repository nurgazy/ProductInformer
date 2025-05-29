package com.nurgazy_bolushbekov.product_informer.api_1C

interface ApiRepository {
    suspend fun ping(): String
    suspend fun info(barcode: String): String
}