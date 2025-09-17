package com.nurgazy_bolushbekov.product_informer.product.entity

import androidx.room.Embedded

data class SpecificationWithProduct(
    @Embedded val specification: ProductSpecification,
    @Embedded val product: Product
)
