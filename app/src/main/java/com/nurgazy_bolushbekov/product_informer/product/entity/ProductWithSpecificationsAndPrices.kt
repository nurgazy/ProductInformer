package com.nurgazy_bolushbekov.product_informer.product.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithSpecificationsAndPrices(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "productId",
        entityColumn = "product_id",
        entity = ProductSpecification::class
    )
    val specificationsWithPrices: List<SpecificationsWithPrices>
)
