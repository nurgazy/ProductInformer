package com.nurgazy_bolushbekov.product_informer.product.repo

import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {

    fun getProductWithSpecificationsByUuid1C(uuid1C: String) = productDao.getProductWithSpecificationsByUuid1C(uuid1C)

    fun getSpecificationWithProduct(uuid1C: String) = productDao.getSpecificationWithProduct(uuid1C)
}