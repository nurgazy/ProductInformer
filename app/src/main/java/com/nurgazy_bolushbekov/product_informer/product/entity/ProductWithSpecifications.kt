package com.nurgazy_bolushbekov.product_informer.product.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithSpecifications(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "productId",
        entityColumn = "product_id"
    )
    val specifications: List<ProductSpecification>
)
