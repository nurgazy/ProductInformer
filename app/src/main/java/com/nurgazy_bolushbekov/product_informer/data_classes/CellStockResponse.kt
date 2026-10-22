package com.nurgazy_bolushbekov.product_informer.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Parcelize
@Serializable
data class CellStockResponse(
    @SerialName("Ячейка")
    val cell: String = "",
    @JsonNames("ВНаличии", "Вналичии", "вналичии")
    @SerialName("ВНаличии")
    val inStock: Double = 0.0
) : Parcelable, java.io.Serializable
