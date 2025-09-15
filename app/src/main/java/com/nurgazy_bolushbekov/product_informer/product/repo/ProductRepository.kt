package com.nurgazy_bolushbekov.product_informer.product.repo

import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    suspend fun getProductByBarcode(barcode: String): Product?{
        return withContext(Dispatchers.IO){
            productDao.getProductByBarcode(barcode)
        }
    }

    suspend fun getProductByUuid1C(uuid1C: String): Product?{
        return withContext(Dispatchers.IO){
            productDao.getProductByUuid1C(uuid1C)
        }
    }
}