package com.nurgazy_bolushbekov.product_informer.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
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
    var savedImagePath: String? = null,

    var productSpecifications: List<ProductSpecification>? = null
) : Parcelable, java.io.Serializable
