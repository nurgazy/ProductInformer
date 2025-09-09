package com.nurgazy_bolushbekov.product_informer.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CellStockResponse(
    @SerialName("Ячейка")
    val cell: String,
    @SerialName("ВНаличии")
    val inStock: Double
) : Parcelable, java.io.Serializable
