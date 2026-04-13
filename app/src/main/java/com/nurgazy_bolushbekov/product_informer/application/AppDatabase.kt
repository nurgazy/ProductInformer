package com.nurgazy_bolushbekov.product_informer.application

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nurgazy_bolushbekov.product_informer.barcode_collection.dao.BarcodeDocDao
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDocDetail
import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductSpecification

@Database(entities = [BarcodeDoc::class, BarcodeDocDetail::class, Product::class, ProductSpecification::class],
    version = 3, exportSchema = true)
abstract class AppDatabase:RoomDatabase() {
    abstract fun barcodeDocDao(): BarcodeDocDao
    abstract fun productDao(): ProductDao
}