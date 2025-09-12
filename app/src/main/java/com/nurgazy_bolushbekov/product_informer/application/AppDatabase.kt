package com.nurgazy_bolushbekov.product_informer.application

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
import com.nurgazy_bolushbekov.product_informer.product.entity.Product

@Database(entities = [Product::class], version = 2, exportSchema = true)
abstract class AppDatabase:RoomDatabase() {
    abstract fun productDao(): ProductDao
}