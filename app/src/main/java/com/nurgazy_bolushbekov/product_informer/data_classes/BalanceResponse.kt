package com.nurgazy_bolushbekov.product_informer.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class BalanceResponse(
    @SerialName("Склад")
    val warehouse: String,
    @SerialName("ВНаличии")
    val inStock: Double,
    @SerialName("Доступно")
    val available: Double,
    @SerialName("Единица")
    val unit: String,
    @SerialName("Ячейки")
    val cellStockResponse: List<CellStockResponse>?=null
) : Parcelable, java.io.Serializable
