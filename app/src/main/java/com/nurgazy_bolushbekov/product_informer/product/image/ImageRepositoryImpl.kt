package com.nurgazy_bolushbekov.product_informer.product.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class ImageRepositoryImpl @Inject constructor(
    private val context: Context
): ImageRepository {

    private val cacheDir:File
        get() = context.cacheDir

    fun getBitmapFromJson(jsonString: JsonElement): Bitmap?{
        val base64String = jsonString.jsonObject["Картинка"].toString()
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            return bitmap
        }catch (e: Exception){
            Log.d("ProductInformer", "Invalid Base64 string: ${e.message}")
            return null
        }
    }


    override suspend fun saveImageToCache(
        bitmap: Bitmap,
        filename: String,
        format: Bitmap.CompressFormat,
        quality: Int
    ): Result<File> = withContext(Dispatchers.IO){
        val file = File(cacheDir, filename)
        var fileOutputStream: FileOutputStream? = null
        return@withContext try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(format, quality, fileOutputStream)
            fileOutputStream.flush()
            Result.success(file)
        } catch (e: IOException) {
            e.printStackTrace()
            Result.failure(e)
        } finally {
            try {
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun loadImageFromCache(filePath: String): Bitmap? = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(filePath)
            if (file.exists()) {
                BitmapFactory.decodeFile(filePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteImageFromCache(filePath: String): Boolean = withContext(Dispatchers.IO){
        val file = File(filePath)
        return@withContext if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
}