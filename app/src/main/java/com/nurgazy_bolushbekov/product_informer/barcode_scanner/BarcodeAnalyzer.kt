package com.nurgazy_bolushbekov.product_informer.barcode_scanner

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

// --- Класс-анализатор для ML Kit ---
class BarcodeAnalyzer(private val isSquare: Boolean = false, private val listener: (String) -> Unit) : ImageAnalysis.Analyzer {

    // Опции: Сканируем только EAN_13 и QR_CODE для скорости
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_QR_CODE
        )
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
                    for (barcode in barcodes) {
                        val boundingBox = barcode.boundingBox ?: continue

                        // Проверяем, находится ли штрихкод в рамке
                        if (isInsideScannerFrame(boundingBox, imageProxy)) {
                            val rawValue = barcode.rawValue
                            if (rawValue != null) {
                                listener(rawValue)
                                break // Берем первый подходящий и выходим
                            }
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

    private fun isInsideScannerFrame(boundingBox: android.graphics.Rect, imageProxy: ImageProxy): Boolean {
        // Получаем размеры кадра с учетом поворота
        val isRotated = imageProxy.imageInfo.rotationDegrees == 90 || imageProxy.imageInfo.rotationDegrees == 270
        val width = if (isRotated) imageProxy.height else imageProxy.width
        val height = if (isRotated) imageProxy.width else imageProxy.height

        val frameLeft: Float
        val frameRight: Float
        val frameTop: Float
        val frameBottom: Float

        if (isSquare) {
            val sizeDim = minOf(width, height) * 0.7f
            frameLeft = (width - sizeDim) / 2
            frameRight = frameLeft + sizeDim
            frameTop = (height - sizeDim) / 2
            frameBottom = frameTop + sizeDim
        } else {
            // Определяем "активное окно" в процентах (должно совпадать с визуальной рамкой)
            // В вашем ScannerOverlay рамка занимает 30% высоты по центру и всю ширину
            val horizontalMargin = width * 0.05f // небольшие отступы по бокам (5%)
            val verticalMargin = height * 0.35f   // (100% - 30% рамки) / 2 = 35% сверху и снизу

            frameLeft = horizontalMargin
            frameRight = width - horizontalMargin
            frameTop = verticalMargin
            frameBottom = height - verticalMargin
        }

        // Проверяем, входит ли центр штрихкода в эти границы
        val barcodeCenterX = boundingBox.centerX().toFloat()
        val barcodeCenterY = boundingBox.centerY().toFloat()

        return barcodeCenterX in frameLeft..frameRight &&
                barcodeCenterY in frameTop..frameBottom
    }
}