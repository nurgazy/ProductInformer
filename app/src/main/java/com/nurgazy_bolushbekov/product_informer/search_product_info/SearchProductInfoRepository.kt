package com.nurgazy_bolushbekov.product_informer.search_product_info

import android.graphics.Bitmap
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiProviderManager
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
import com.nurgazy_bolushbekov.product_informer.product.entity.Price
import com.nurgazy_bolushbekov.product_informer.product.entity.Product
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductSpecification
import com.nurgazy_bolushbekov.product_informer.product.image.ImageRepositoryImpl
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class SearchProductInfoRepository @Inject constructor(
    private val apiProviderManager: ApiProviderManager,
    private val imageRepository: ImageRepositoryImpl,
    private val productDao: ProductDao
) {

    suspend fun refreshProduct(barcode: String, fullSpecifications: Boolean?): ResultFetchData<String> {
        return withContext(Dispatchers.IO){
            try {
                val apiService = apiProviderManager.apiService.filterNotNull().first()
                val response: Response<ResponseBody> = apiService.info(barcode, fullSpecifications)
                if (response.isSuccessful){
                    val responseString = response.body()!!.string()
                    val jsonObj = Json { ignoreUnknownKeys = true }
                    val jsonString = jsonObj.parseToJsonElement(responseString)

                    val productFound = jsonString.jsonObject["Результат"].toString().toBoolean()
                    if (!productFound){
                        return@withContext ResultFetchData.Error(Exception("Товар не найден"))
                    }

                    val productResponse: ProductResponse = jsonObj.decodeFromString(jsonString.jsonObject["Номенклатура"].toString())
                    productResponse.productSpecificResponses = jsonObj.decodeFromString(jsonString.jsonObject["Характеристики"].toString())

                    if (jsonString.jsonObject.containsKey("Картинка")){
                        val bitmap = imageRepository.getBitmapFromJson(jsonString)

                        if (bitmap != null) {
                            val resultSaveImage = imageRepository.saveImageToCache(
                                bitmap,
                                "${productResponse.uuid1C}.jpeg",
                                Bitmap.CompressFormat.JPEG,
                                90)

                            resultSaveImage.fold(
                                onSuccess = { file ->
                                    productResponse.savedImagePath = file.absolutePath
                                },
                                onFailure = { _ ->
                                    productResponse.savedImagePath = null
                                }
                            )
                        }
                    }

                    val product = Product(
                        productName = productResponse.name,
                        barcode = productResponse.barcode,
                        article = productResponse.article,
                        manufacturer = productResponse.manufacturer,
                        brand = productResponse.brand,
                        productCategory = productResponse.productCategory,
                        productUuid1C = productResponse.uuid1C,
                        savedImagePath = productResponse.savedImagePath
                    )

                    val productSpecPrices = mutableMapOf<ProductSpecification, List<Price>>()
                    productResponse.productSpecificResponses!!.forEach { curSpecResponse ->
                        val curSpecification = ProductSpecification(
                            name = curSpecResponse.name,
                            uuid1C = curSpecResponse.uuid1C,
                            specificationBarcode = curSpecResponse.barcode
                        )

                        val curPrices = mutableListOf<Price>()
                        curSpecResponse.priceResponse!!.forEach{ curPrice ->
                            curPrices.add(
                                Price(
                                    priceType = curPrice.priceType,
                                    price = curPrice.price,
                                    currency = curPrice.currency
                                )
                            )
                        }

                        productSpecPrices[curSpecification] = curPrices
                    }

//                    val productSpecificationList: List<ProductSpecification> = productResponse.productSpecificResponses!!.map {productSpecificResponse ->
//                        ProductSpecification(
//                            name = productSpecificResponse.name,
//                            uuid1C = productSpecificResponse.uuid1C
//                        )
//                    }


                    productDao.saveProductWithSpecificationsAndPrices(product, productSpecPrices)
//                    productDao.saveProductWithSpecifications(product, productSpecificationList)
                    return@withContext ResultFetchData.Success(product.productUuid1C)
                }else{
                    return@withContext ResultFetchData.Error(Exception("Ошибка. Код: ${response.code()}. ${response.message()}"))
                }
            } catch (e: Exception){
                return@withContext ResultFetchData.Error(e)
            }
        }
    }

}