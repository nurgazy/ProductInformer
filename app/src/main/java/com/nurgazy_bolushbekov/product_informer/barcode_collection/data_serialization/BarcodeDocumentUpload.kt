package com.nurgazy_bolushbekov.product_informer.barcode_collection.data_serialization

import kotlinx.serialization.Serializable

@Serializable
data class BarcodeDocumentUpload(
    val internalId: Long,
    val uuid1C: String,
    val userName: String,
    val items: List<BarcodeDocumentItem>
)

@Serializable
data class BarcodeDocumentItem(
    val name: String,
    val barcode: String,
    val productUuid1C: String,
    val productSpecUuid1C: String,
    val quantity: Int
)