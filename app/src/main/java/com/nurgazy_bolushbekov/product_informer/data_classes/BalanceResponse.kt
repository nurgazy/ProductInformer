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
data class BalanceResponse(
    @SerialName("Склад")
    val warehouse: String = "",
    @JsonNames("ВНаличии", "Вналичии", "вналичии")
    @SerialName("ВНаличии")
    val inStock: Double = 0.0,
    @SerialName("Доступно")
    val available: Double = 0.0,
    @SerialName("Единица")
    val unit: String = "",
    @Serializable(with = CellStockListSerializer::class)
    @SerialName("Ячейки")
    val cellStockResponse: List<CellStockResponse>? = null
) : Parcelable, java.io.Serializable
