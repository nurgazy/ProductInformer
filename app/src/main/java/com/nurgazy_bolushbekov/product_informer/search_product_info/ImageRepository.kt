package com.nurgazy_bolushbekov.product_informer.search_product_info

import android.graphics.Bitmap
import java.io.File

interface ImageRepository {

    suspend fun saveImageToCache(
        bitmap: Bitmap,
        filename: String,
        format: Bitmap.CompressFormat,
        quality: Int
    ): Result<File> // Используем Result для обработки успеха/ошибки

    suspend fun loadImageFromCache(filePath: String): Bitmap?

    suspend fun deleteImageFromCache(filePath: String): Boolean
}