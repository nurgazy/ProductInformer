package com.nurgazy_bolushbekov.product_informer.application

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products ADD COLUMN savedImagePath TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS product_specification (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, uuid1C TEXT NOT NULL, product_id INTEGER NOT NULL)")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE INDEX index_product_specification_product_id ON product_specification (product_id)")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE product_specification")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS product_specification (\n" +
                "    `id` INTEGER NOT NULL,\n" +
                "    `name` TEXT NOT NULL,\n" +
                "    `uuid1C` TEXT NOT NULL,\n" +
                "    `product_id` INTEGER NOT NULL,\n" +
                "    PRIMARY KEY(`id`),\n" +
                "    FOREIGN KEY(`product_id`) REFERENCES `products`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE\n" +
                ")")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE UNIQUE INDEX index_products_uuid1C ON products (uuid1C)")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE UNIQUE INDEX index_product_specification_uuid1C ON product_specification (uuid1C)")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products RENAME COLUMN id TO productId")
        db.execSQL("ALTER TABLE product_specification RENAME COLUMN id TO specificationId")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products RENAME COLUMN productId TO id")
        db.execSQL("ALTER TABLE product_specification RENAME COLUMN specificationId TO id")
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products RENAME COLUMN id TO productId")
        db.execSQL("ALTER TABLE products RENAME COLUMN name TO productName")
        db.execSQL("ALTER TABLE products RENAME COLUMN uuid1c TO productUuid1C")
    }
}