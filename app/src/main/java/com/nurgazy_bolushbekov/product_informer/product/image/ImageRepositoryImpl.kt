package com.nurgazy_bolushbekov.product_informer.product.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class ImageRepositoryImpl @Inject constructor(
    private val context: Context
): ImageRepository {

    private val cacheDir: File
        get() = context.cacheDir

    fun getBitmapFromJson(
        jsonString: JsonElement,
        reqWidth: Int = 1024,
        reqHeight: Int = 1024
    ): Bitmap? {
        val rawBase64 = try {
            jsonString.jsonObject["Картинка"]?.jsonPrimitive?.content
                ?: jsonString.jsonObject["Картинка"]?.toString()?.trim('"')
        } catch (_: Exception) {
            null
        } ?: return null

        return try {
            val decodedBytes = Base64.decode(rawBase64, Base64.DEFAULT)

            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size, options)

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false

            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size, options)
        } catch (e: Exception) {
            Log.d("ProductInformer", "Invalid Base64 string: ${e.message}")
            null
        }
    }


    override suspend fun saveImageToCache(
        bitmap: Bitmap,
        filename: String,
        format: Bitmap.CompressFormat,
        quality: Int
    ): Result<File> = withContext(Dispatchers.IO) {
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
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(filePath, options)

                options.inSampleSize = calculateInSampleSize(options, 1024, 1024)
                options.inJustDecodeBounds = false

                BitmapFactory.decodeFile(filePath, options)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteImageFromCache(filePath: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(filePath)
        return@withContext if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
