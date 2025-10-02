package com.nurgazy_bolushbekov.product_informer.barcode_collection.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BarcodeWithDetails(
    @Embedded val barcodeDoc: BarcodeDoc,
    @Relation(
        parentColumn = "barcodeDocId",
        entityColumn = "barcodeDocId"
    )
    val details: List<BarcodeDocDetail>
)
