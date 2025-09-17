package com.nurgazy_bolushbekov.product_informer.product.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductSpecification
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductWithSpecifications
import com.nurgazy_bolushbekov.product_informer.product.entity.SpecificationWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    suspend fun getProductByBarcode(barcode: String): Product?

    @Query("SELECT * FROM products WHERE productUuid1C = :uuid1C")
    suspend fun getProductByUuid1C(uuid1C: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductSpecifications(specifications: List<ProductSpecification>)

    @Transaction
    suspend fun saveProductWithSpecifications(product: Product, specifications: List<ProductSpecification>) {
        val productId = insertProduct(product)
        val productSpecifications = specifications.map { it.copy(productId = productId) }
        insertProductSpecifications(productSpecifications)
    }

    @Transaction
    @Query("SELECT * FROM products WHERE productUuid1C = :uuid1C")
    fun getProductWithSpecificationsByUuid1C(uuid1C: String): Flow<ProductWithSpecifications>

    @Transaction
    @Query("""SELECT * FROM product_specification INNER JOIN products ON product_specification.product_id = products.productId WHERE product_specification.uuid1C = :uuid1C""")
    fun getSpecificationWithProduct(uuid1C: String): Flow<SpecificationWithProduct>
}