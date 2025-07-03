package com.nurgazy_bolushbekov.product_informer.product.image

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageRepositoryImpl(private val application: Application): ImageRepository {

    private val cacheDir:File
        get() = application.cacheDir


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