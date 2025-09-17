package com.nurgazy_bolushbekov.product_informer.search_product_info

import android.graphics.Bitmap
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiProviderManager
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
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

                    val productFound: Boolean = jsonString.jsonObject["Результат"].toString().toBoolean()
                    if (!productFound){
                        return@withContext ResultFetchData.Error(Exception("Товар не найден"))
                    }

                    val productResponse: ProductResponse = jsonObj.decodeFromString(jsonString.jsonObject["Номенклатура"].toString())
                    productResponse.productSpecificResponses = jsonObj.decodeFromString(jsonString.jsonObject["Характеристики"].toString())

                    val productSpecificationList: List<ProductSpecification> = productResponse.productSpecificResponses!!.map {productSpecificResponse ->
                        ProductSpecification(
                            name = productSpecificResponse.name,
                            uuid1C = productSpecificResponse.uuid1C
                        )
                    }

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
                    productDao.saveProductWithSpecifications(productResponse.toProduct(), productSpecificationList)
                    return@withContext ResultFetchData.Success(productResponse.uuid1C)
                }else{
                    return@withContext ResultFetchData.Error(Exception("Ошибка. Код: ${response.code()}. ${response.message()}"))
                }
            } catch (e: Exception){
                return@withContext ResultFetchData.Error(e)
            }
        }
    }

}