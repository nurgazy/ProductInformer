package com.nurgazy_bolushbekov.product_informer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.nurgazy_bolushbekov.product_informer.barcode_scanner.BarcodeAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val BARCODE_RESULT_KEY = "BARCODE_RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем Executor для асинхронной обработки кадров
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Создаем и добавляем PreviewView, который будет отображать Compose
        previewView = PreviewView(this)

        // Устанавливаем Compose UI
        setContentView(previewView)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && allPermissionsGranted()) {
            startCamera()
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            // Если разрешение не дано, возвращаемся назад
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    // --- Инициализация CameraX и ML Kit ---
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Настройка Preview (отображение на экране)
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

            // Настройка ImageAnalysis (поток для ML Kit)
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Анализировать только последний кадр
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer(::onBarcodeScanned))
                }

            // Выбор камеры (задняя)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Отвязываем все привязки перед новой привязкой
                cameraProvider.unbindAll()

                // Привязываем варианты использования к жизненному циклу
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, // Activity является владельцем жизненного цикла
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

            } catch (exc: Exception) {
                Log.e("ProductInformer", "Ошибка привязки камеры: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // --- Обработка результата и возврат в Compose ---
    private fun onBarcodeScanned(barcode: String) {
        // Останавливаем обработку после успешного сканирования, чтобы избежать повторов
        cameraExecutor.shutdown()

        val resultIntent = Intent().apply {
            putExtra(BARCODE_RESULT_KEY, barcode)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    // --- Завершение ---
    override fun onDestroy() {
        super.onDestroy()
        // Обязательно закрываем Executor
        cameraExecutor.shutdown()
    }
}

