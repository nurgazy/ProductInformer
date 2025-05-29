package com.nurgazy_bolushbekov.product_informer.price_checker

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun BarcodeScannerScreen(priceCheckerVM: PriceCheckerViewModel){
    val context = LocalContext.current

    val isCameraPermissionGranted = checkCameraPermission()
    val cameraPermissionGranted = remember { mutableStateOf(isCameraPermissionGranted) }

    val isScannerVisible = remember { mutableStateOf(false) }

    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract =  ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraPermissionGranted.value = isGranted
            if (isGranted) {
                Log.d("ProductInformer", "Camera permission granted")
            } else {
                Log.d("ProductInformer", "Camera permission denied")
            }
        })

    LaunchedEffect(Unit) {
        val cameraPermissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (cameraPermissionStatus == PackageManager.PERMISSION_GRANTED) {
            cameraPermissionGranted.value = true
        } else {
            cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (cameraPermissionGranted.value) {
        if (isScannerVisible.value) {
            BackHandler(enabled = isScannerVisible.value) {
                // Обработка нажатия "Назад" во время видимости сканера
                isScannerVisible.value = false
            }
            BarcodeScannerContent(
                onBarcodeScanned = { barcode ->
                    isScannerVisible.value = false // Закрываем сканер после получения результата
                    priceCheckerVM.onChangeBarcode(barcode)
                    priceCheckerVM.getInfo()
                },
                onCloseScanner = {
                    isScannerVisible.value = false // Обработка случая, если сканер закрывается без сканирования
                }
            )
        } else {
            PriceCheckerContent(priceCheckerVM, isScannerVisible)
        }
    } else {
        PermissionDeniedScreen()
    }
}

@Composable
fun checkCameraPermission(): Boolean {
    val context = LocalContext.current
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerContent(
    onBarcodeScanned: (String) -> Unit,
    onCloseScanner: () -> Unit)
{
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }
    val isScanning = remember { mutableStateOf(true) }

    LaunchedEffect(isScanning.value) {
        if (!isScanning.value) {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            Log.d("ProductInformer", "Камера закрыта после сканирования")
            onCloseScanner() // Сообщаем вызывающему компоненту, что сканер закрыт
        }
    }

    val analyzer = remember {
        ImageAnalysis.Analyzer { imageProxy ->
            if (isScanning.value) {
                processImageProxy(barcodeScanner, imageProxy) { barcode ->
                    if (barcode != null) {
                        isScanning.value = false
                        barcode.rawValue?.let { onBarcodeScanned(it) }
                    }
                }
            } else {
                imageProxy.close() // Важно закрывать ImageProxy, когда сканер неактивен
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().apply {
                        surfaceProvider = previewView.surfaceProvider
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .apply {
                            setAnalyzer(executor, analyzer)
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.d("ProductInformer", "Binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

    }
}

@OptIn(ExperimentalGetImage::class)
fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onBarcodeDetected: (Barcode?) -> Unit
) {
    val inputImage = imageProxy.image
    if (inputImage != null) {
        val image = InputImage.fromMediaImage(
            inputImage,
            imageProxy.imageInfo.rotationDegrees
        )

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.let {
                    onBarcodeDetected(barcodes.first()) // Обрабатываем первый найденный штрихкод
                }
            }
            .addOnFailureListener { e ->
                Log.d("ProductInformer", "Barcode scanning failed", e)
                onBarcodeDetected(null)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
        onBarcodeDetected(null)
    }
}

@Composable
fun PermissionDeniedScreen() {
    Text(text = "Для сканирования штрихкодов необходимо разрешение на использование камеры.")
    Log.d("ProductInformer", "Для сканирования штрихкодов необходимо разрешение на использование камеры.")
}
