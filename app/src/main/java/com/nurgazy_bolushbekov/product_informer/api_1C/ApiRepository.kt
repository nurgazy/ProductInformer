package com.nurgazy_bolushbekov.product_informer.api_1C

import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.flow.Flow

interface ApiRepository {
    suspend fun ping(): ResultFetchData<String>
    suspend fun info(barcode: String, fullSpecifications: Boolean? = null): Flow<ResultFetchData<ProductResponse>>
}