package com.nurgazy_bolushbekov.product_informer.data_classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductSpecification(
    @SerialName("Наименование")
    val name: String,

    @SerialName("Остатки")
    val balance: List<Balance>?=null,

    @SerialName("Цены")
    val price: List<Price>?=null
)
