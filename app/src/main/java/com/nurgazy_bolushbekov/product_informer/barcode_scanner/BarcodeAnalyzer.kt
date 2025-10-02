package com.nurgazy_bolushbekov.product_informer.barcode_scanner

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

// --- Класс-анализатор для ML Kit ---
class BarcodeAnalyzer(private val listener: (String) -> Unit) : ImageAnalysis.Analyzer {

    // Опции: Сканируем только EAN_13 и QR_CODE для скорости
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        // Получили первый штрихкод и отправляем его
                        val rawValue = barcodes.first().rawValue
                        if (rawValue != null) {
                            listener(rawValue) // <--- Вызов метода onBarcodeScanned в Activity!
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("BarcodeAnalyzer", "Ошибка сканирования: ${it.message}")
                }
                .addOnCompleteListener {
                    // Обязательно закрываем кадр, чтобы получить следующий
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}