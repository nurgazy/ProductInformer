package com.nurgazy_bolushbekov.product_informer.application

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE barcode ADD COLUMN comment TEXT DEFAULT ''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Создаем таблицу продуктов
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS 'products' (
                'productId' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                'productName' TEXT NOT NULL, 
                'barcode' TEXT NOT NULL, 
                'article' TEXT NOT NULL, 
                'manufacturer' TEXT NOT NULL, 
                'brand' TEXT NOT NULL, 
                'productCategory' TEXT NOT NULL, 
                'productUuid1C' TEXT NOT NULL, 
                'savedImagePath' TEXT
            )
        """.trimIndent())

        // Создаем индекс для уникальности UUID
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS 'index_products_productUuid1C' ON 'products' ('productUuid1C')")

        // Создаем таблицу характеристик
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS 'product_specification' (
                'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                'name' TEXT NOT NULL, 
                'uuid1C' TEXT NOT NULL, 
                'specificationBarcode' TEXT NOT NULL, 
                'product_id' INTEGER NOT NULL, 
                FOREIGN KEY('product_id') REFERENCES 'products'('productId') ON UPDATE NO ACTION ON DELETE CASCADE 
            )
        """.trimIndent())

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS 'index_product_specification_uuid1C' ON 'product_specification' ('uuid1C')")
    }
}