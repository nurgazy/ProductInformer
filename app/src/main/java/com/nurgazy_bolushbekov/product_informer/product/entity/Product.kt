package com.nurgazy_bolushbekov.product_informer.product.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val barcode: String,
    val article: String,
    val manufacturer: String,
    val brand: String,
    val productCategory: String,
    val uuid1C: String,
    val savedImagePath: String?
)
