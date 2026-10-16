package com.nurgazy_bolushbekov.product_informer.barcode_collection.data_serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarcodeDocumentUpload(
    @SerialName("internalId") val internalId: Long,
    @SerialName("uuid1C") val uuid1C: String,
    @SerialName("userName") val userName: String,
    @SerialName("docDate") val docDate: String,
    @SerialName("comment") val comment: String = "",
    @SerialName("items") val items: List<BarcodeDocumentItem>
)

@Serializable
data class BarcodeDocumentItem(
    @SerialName("name") val name: String,
    @SerialName("barcode") val barcode: String,
    @SerialName("productUuid1C") val productUuid1C: String,
    @SerialName("productSpecUuid1C") val productSpecUuid1C: String,
    @SerialName("quantity") val quantity: Int
)
