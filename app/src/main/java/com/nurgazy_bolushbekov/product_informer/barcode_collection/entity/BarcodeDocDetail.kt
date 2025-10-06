package com.nurgazy_bolushbekov.product_informer.barcode_collection.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "barcodeDetails",
    foreignKeys = [ForeignKey(entity = BarcodeDoc::class, parentColumns = ["barcodeDocId"], childColumns = ["barcodeDocId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["barcodeDocId"])]
)
data class BarcodeDocDetail(
    @PrimaryKey(autoGenerate = true)
    val barcodeDetailId: Long = 0,
    val barcode: String,
    val productName: String,
    val productSpecName: String,
    val productUuid1C: String,
    val productSpecUuid1C: String,
    val barcodeDocId: Long=0,
    val quantity: Int = 0
)
