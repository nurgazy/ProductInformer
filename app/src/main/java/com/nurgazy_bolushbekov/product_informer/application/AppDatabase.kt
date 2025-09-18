package com.nurgazy_bolushbekov.product_informer.application

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
import com.nurgazy_bolushbekov.product_informer.product.entity.Price
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductSpecification

@Database(entities = [Product::class, ProductSpecification::class, Price::class], version = 14, exportSchema = true)
abstract class AppDatabase:RoomDatabase() {
    abstract fun productDao(): ProductDao
}