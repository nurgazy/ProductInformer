package com.nurgazy_bolushbekov.product_informer.product.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nurgazy_bolushbekov.product_informer.product.entity.Price
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductSpecification
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductWithSpecifications
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductWithSpecificationsAndPrices
import com.nurgazy_bolushbekov.product_informer.product.entity.SpecificationWithProduct
import com.nurgazy_bolushbekov.product_informer.product.entity.SpecificationsWithPrices
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductSpecification(specification: ProductSpecification): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductSpecifications(specifications: List<ProductSpecification>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrices(prices: List<Price>)

    @Transaction
    suspend fun saveProductWithSpecifications(product: Product, specifications: List<ProductSpecification>) {
        val productId = insertProduct(product)
        val productSpecifications = specifications.map { it.copy(productId = productId) }
        insertProductSpecifications(productSpecifications)
    }

    @Transaction
    suspend fun saveProductWithSpecificationsAndPrices(product: Product, specifications: Map<ProductSpecification, List<Price>>) {
        val productId = insertProduct(product)
        specifications.forEach { (specification, prices) ->
            val newSpecification = specification.copy(productId = productId)
            val specificationId = insertProductSpecification(newSpecification)

            val pricesToSave = prices.map { price ->
                price.copy(productId = productId, specificationId = specificationId)
            }
            insertPrices(pricesToSave)
        }
    }


    @Query("SELECT * FROM products WHERE barcode = :barcode")
    suspend fun getProductByBarcode(barcode: String): Product?

    @Query("SELECT * FROM products WHERE productUuid1C = :uuid1C")
    suspend fun getProductByUuid1C(uuid1C: String): Product?

    @Transaction
    @Query("SELECT * FROM products WHERE productUuid1C = :uuid1C")
    fun getProductWithSpecificationsByUuid1C(uuid1C: String): Flow<ProductWithSpecifications>

    @Transaction
    @Query("""SELECT * FROM product_specification INNER JOIN products ON product_specification.product_id = products.productId WHERE product_specification.uuid1C = :uuid1C""")
    fun getSpecificationWithProduct(uuid1C: String): Flow<SpecificationWithProduct>

    @Transaction
    @Query("SELECT * FROM products WHERE productUuid1C = :uuid1C")
    fun getProductWithSpecificationsAndPricesByUuid1C(uuid1C: String): Flow<ProductWithSpecificationsAndPrices>

    @Transaction
    @Query("SELECT * FROM product_specification INNER JOIN products ON product_specification.product_id = products.productId  WHERE product_specification.specificationBarcode = :barcode")
    fun getProductWithSpecificationsByBarcode(barcode: String): Flow<Map<Product, SpecificationsWithPrices>>

}