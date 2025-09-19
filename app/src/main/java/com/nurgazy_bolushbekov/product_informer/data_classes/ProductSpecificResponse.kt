package com.nurgazy_bolushbekov.product_informer.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ProductSpecificResponse(
    @SerialName("Наименование")
    val name: String,

    @SerialName("ГУИД1С")
    val uuid1C: String,

    @SerialName("Штрихкод")
    val barcode: String="",

    @SerialName("Остатки")
    val balanceResponse: List<BalanceResponse>?=null,

    @SerialName("Цены")
    val priceResponse: List<PriceResponse>?=null
) : Parcelable, java.io.Serializable
