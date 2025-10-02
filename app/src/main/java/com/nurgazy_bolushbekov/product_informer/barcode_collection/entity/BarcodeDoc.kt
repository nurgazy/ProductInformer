package com.nurgazy_bolushbekov.product_informer.barcode_collection.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nurgazy_bolushbekov.product_informer.utils.BarcodeStatus

@Entity(
    tableName = "barcode",
)
data class BarcodeDoc(
    @PrimaryKey(autoGenerate = true)
    val barcodeDocId: Long = 0,
    val status: BarcodeStatus,
)
