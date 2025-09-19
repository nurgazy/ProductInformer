package com.nurgazy_bolushbekov.product_informer.product.repo

import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {

    fun getProductWithSpecificationsAndPricesByUuid1C(uuid1C: String) = productDao.getProductWithSpecificationsAndPricesByUuid1C(uuid1C)

    fun getProductWithSpecificationsByBarcode(barcode: String) = productDao.getProductWithSpecificationsByBarcode(barcode)
}