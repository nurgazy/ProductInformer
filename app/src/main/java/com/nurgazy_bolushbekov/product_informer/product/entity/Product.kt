package com.nurgazy_bolushbekov.product_informer.product.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [Index(value = ["productUuid1C"], unique = true)]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val productId: Long = 0,
    val productName: String,
    val barcode: String,
    val article: String,
    val manufacturer: String,
    val brand: String,
    val productCategory: String,
    val productUuid1C: String,
    val savedImagePath: String?
)
