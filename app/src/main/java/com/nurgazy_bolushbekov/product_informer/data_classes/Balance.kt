package com.nurgazy_bolushbekov.product_informer.data_classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Balance(
    @SerialName("Склад")
    val warehouse: String,
    @SerialName("ВНаличии")
    val inStock: Double,
    @SerialName("Доступно")
    val available: Double,
    @SerialName("Единица")
    val unit: String
)
