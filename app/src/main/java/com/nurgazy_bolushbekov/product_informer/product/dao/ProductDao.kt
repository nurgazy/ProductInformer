package com.nurgazy_bolushbekov.product_informer.product.dao

import androidx.room.*
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductSpecification
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductWithSpecifications
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecifications(specifications: List<ProductSpecification>)

    @Transaction
    suspend fun insertProductWithSpecs(product: Product, specs: List<ProductSpecification>) {
        val productId = insertProduct(product)
        val specsWithId = specs.map { it.copy(productId = productId) }
        insertSpecifications(specsWithId)
    }

    @Transaction
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ProductWithSpecifications?

    @Transaction
    @Query("SELECT * FROM products WHERE productUuid1C = :uuid LIMIT 1")
    suspend fun getProductByUuid(uuid: String): ProductWithSpecifications?

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()

    @Transaction
    @Query("""
        SELECT * FROM products 
        WHERE productId IN (
            SELECT product_id 
            FROM product_specification 
            WHERE specificationBarcode = :barcode
        ) 
        LIMIT 1
    """)
    suspend fun getProductBySpecBarcode(barcode: String): ProductWithSpecifications?

    @Query("SELECT * FROM products ORDER BY productName ASC")
    fun getAllProducts(): Flow<List<Product>>
}