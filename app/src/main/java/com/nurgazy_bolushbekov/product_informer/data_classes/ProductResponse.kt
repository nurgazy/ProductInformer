package com.nurgazy_bolushbekov.product_informer.data_classes

import android.os.Parcelable
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ProductResponse(
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
    @SerialName("ГУИД1С")
    val uuid1C: String,
    var savedImagePath: String? = null,

    var productSpecificResponses: List<ProductSpecificResponse>? = null
) : Parcelable, java.io.Serializable
