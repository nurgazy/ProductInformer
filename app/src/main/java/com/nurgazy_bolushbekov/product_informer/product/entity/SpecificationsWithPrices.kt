package com.nurgazy_bolushbekov.product_informer.product.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SpecificationsWithPrices(
    @Embedded val specification: ProductSpecification,
    @Relation(
        parentColumn = "id",
        entityColumn = "specificationId",
        entity = Price::class
    )
    val prices: List<Price>
)
