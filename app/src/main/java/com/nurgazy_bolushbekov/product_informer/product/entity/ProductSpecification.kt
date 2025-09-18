package com.nurgazy_bolushbekov.product_informer.product.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_specification",
    foreignKeys = [ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["product_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["uuid1C"], unique = true)]
)
data class ProductSpecification(
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val name: String,
    val uuid1C: String,
    val specificationBarcode: String = "",
    @ColumnInfo(name = "product_id")
    val productId: Long=0
)
