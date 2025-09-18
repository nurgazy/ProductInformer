package com.nurgazy_bolushbekov.product_informer.product.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "prices",
    foreignKeys = [
        ForeignKey(entity = ProductSpecification::class, parentColumns = ["id"], childColumns = ["specificationId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["productId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class Price(
    @PrimaryKey(autoGenerate = true)
    val priceId: Long=0,
    val priceType: String,
    val price: Double,
    val currency: String,
    val productId: Long=0,
    val specificationId: Long = 0
)
