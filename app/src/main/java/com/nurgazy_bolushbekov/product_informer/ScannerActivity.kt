package com.nurgazy_bolushbekov.product_informer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.nurgazy_bolushbekov.product_informer.barcode_scanner.BarcodeAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView

    companion object {
        private const val BARCODE_RESULT_KEY = "BARCODE_RESULT"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                setResult(RESULT_CANCELED)
                finish()
            }
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
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED


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
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer(::onBarcodeScanned))
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                // Привязываем варианты использования к жизненному циклу
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner,
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
        if (!cameraExecutor.isShutdown) {
            cameraExecutor.shutdown()
        }
    }
}

