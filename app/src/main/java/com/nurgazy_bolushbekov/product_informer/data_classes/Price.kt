package com.nurgazy_bolushbekov.product_informer.data_classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Price(
    @SerialName("ВидЦены")
    val priceType: String,
    @SerialName("Цена")
    val price: Double,
    @SerialName("Валюта")
    val currency: String
)
