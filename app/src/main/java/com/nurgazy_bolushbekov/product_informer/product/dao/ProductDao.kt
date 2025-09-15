package com.nurgazy_bolushbekov.product_informer.product.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nurgazy_bolushbekov.product_informer.product.entity.Product

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    suspend fun getProductByBarcode(barcode: String): Product?

    @Query("SELECT * FROM products WHERE uuid1C = :uuid1C")
    suspend fun getProductByUuid1C(uuid1C: String): Product?

}