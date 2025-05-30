package com.nurgazy_bolushbekov.product_informer.data_classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("Наименование")
    val name: String,
    @SerialName("Штрихкод")
    val barcode: String,
    @SerialName("Артикул")
    val article: String,
    @SerialName("Производитель")
    val manufacturer: String,
    @SerialName("Марка")
    val brand: String,
    @SerialName("ТоварнаяКатегория")
    val productCategory: String,

    var productSpecifications: List<ProductSpecification>? = null
)
