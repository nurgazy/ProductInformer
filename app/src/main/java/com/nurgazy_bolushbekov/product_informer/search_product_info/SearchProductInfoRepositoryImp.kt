package com.nurgazy_bolushbekov.product_informer.search_product_info

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import com.nurgazy_bolushbekov.product_informer.api_1C.RetrofitClient
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import com.nurgazy_bolushbekov.product_informer.product.image.ImageRepository
import com.nurgazy_bolushbekov.product_informer.product.image.ImageRepositoryImpl
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response

class SearchProductInfoRepositoryImp(username:String, password:String, baseUrl:String, application: Application): ApiRepository {

    private val imageRepository: ImageRepository = ImageRepositoryImpl(application)

    private var apiService = RetrofitClient.create(username, password, baseUrl)

    override suspend fun ping(): ResultFetchData<String> {
        throw NotImplementedError("Not yet implemented")
    }

    override suspend fun info(barcode: String, fullSpecifications: Boolean?): Flow<ResultFetchData<Product>> = flow{
        emit(ResultFetchData.Loading)

        val response: Response<ResponseBody> = withContext(Dispatchers.IO){
            apiService.info(barcode, fullSpecifications)
        }

        if (response.isSuccessful){
            val responseString = response.body()!!.string()
            val jsonObj = Json { ignoreUnknownKeys = true }
            val jsonString = jsonObj.parseToJsonElement(responseString)

            val productFound: Boolean = jsonString.jsonObject["Результат"].toString().toBoolean()
            if (!productFound){
                emit(ResultFetchData.Error(Exception("Товар не найден")))
                return@flow
            }
            val product: Product = jsonObj.decodeFromString(jsonString.jsonObject["Номенклатура"].toString())
            product.productSpecifications = jsonObj.decodeFromString(jsonString.jsonObject["Характеристики"].toString())

            if (jsonString.jsonObject.containsKey("Картинка")){
                val base64String = jsonString.jsonObject["Картинка"].toString()
                var bitmap: Bitmap? = null
                try {
                    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                    bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                }catch (e: Exception){
                    Log.d("ProductInformer", "Invalid Base64 string: ${e.message}")
                }

                if (bitmap != null) {
                    withContext(Dispatchers.IO){
                        val resultSaveImage = imageRepository.saveImageToCache(
                            bitmap,
                            "${product.article}.jpeg",
                            Bitmap.CompressFormat.JPEG,
                            90)

                        resultSaveImage.fold(
                            onSuccess = { file ->
                                product.savedImagePath = file.absolutePath
                            },
                            onFailure = { _ ->
                                product.savedImagePath = null
                            }
                        )
                    }
                } else {
                    product.savedImagePath = null
                }
            }

            emit(ResultFetchData.Success(product))
        }else{
            emit(ResultFetchData.Error(Exception("Ошибка. Код: ${response.code()}. ${response.message()}")))
        }
    }.catch { e ->
        emit(ResultFetchData.Error(e))
    }.flowOn(Dispatchers.IO)
}