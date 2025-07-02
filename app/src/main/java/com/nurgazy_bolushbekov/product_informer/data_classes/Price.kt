package com.nurgazy_bolushbekov.product_informer.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Price(
    @SerialName("ВидЦены")
    val priceType: String,
    @SerialName("Цена")
    val price: Double,
    @SerialName("Валюта")
    val currency: String
) : Parcelable, java.io.Serializable
