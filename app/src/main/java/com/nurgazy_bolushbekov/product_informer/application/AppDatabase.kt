package com.nurgazy_bolushbekov.product_informer.application

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nurgazy_bolushbekov.product_informer.barcode_collection.dao.BarcodeDocDao
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDocDetail

@Database(entities = [BarcodeDoc::class, BarcodeDocDetail::class], version = 1, exportSchema = true)
abstract class AppDatabase:RoomDatabase() {
    abstract fun barcodeDocDao(): BarcodeDocDao
}