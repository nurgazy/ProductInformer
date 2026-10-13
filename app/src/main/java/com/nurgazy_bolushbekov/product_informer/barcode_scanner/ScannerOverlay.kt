package com.nurgazy_bolushbekov.product_informer.barcode_scanner

import androidx.compose.foundation.layout.offset

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    isSquare: Boolean = false,
    scannedBarcode: String? = null,
    onBarcodeConfirmed: (String) -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val context = LocalContext.current
        val canvasWidth = maxWidth
        val canvasHeight = maxHeight

        val rectHeight = if (isSquare) {
            minOf(canvasWidth, canvasHeight) * 0.7f
        } else {
            canvasHeight * 0.3f
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()

            val rectWidthPx: Float
            val rectHeightPx: Float
            val left: Float
            val top: Float

            if (isSquare) {
                val sizeDim = minOf(size.width, size.height) * 0.7f
                rectWidthPx = sizeDim
                rectHeightPx = sizeDim
                left = (size.width - rectWidthPx) / 2
                top = (size.height - rectHeightPx) / 2
            } else {
                // Ширина на весь экран, высота — 30% от высоты экрана
                rectWidthPx = size.width
                rectHeightPx = size.height * 0.3f
                left = 0f
                top = (size.height - rectHeightPx) / 2
            }

            val rect = Rect(Offset(left, top), Size(rectWidthPx, rectHeightPx))

            drawContext.canvas.nativeCanvas.apply {
                val checkpoint = saveLayer(null, null)
                drawRect(Color.Black.copy(alpha = 0.6f))

                // Прозрачное окно
                drawRect(
                    color = Color.Transparent,
                    topLeft = rect.topLeft,
                    size = rect.size,
                    blendMode = BlendMode.Clear
                )
                restoreToCount(checkpoint)
            }

            if (isSquare) {
                drawRect(
                    color = Color.White,
                    topLeft = rect.topLeft,
                    size = rect.size,
                    style = Stroke(width = strokeWidth)
                )
            } else {
                // Рисуем только верхнюю и нижнюю границы, так как боковые за краем
                drawLine(
                    color = Color.White,
                    start = Offset(0f, top),
                    end = Offset(size.width, top),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color.White,
                    start = Offset(0f, top + rectHeightPx),
                    end = Offset(size.width, top + rectHeightPx),
                    strokeWidth = strokeWidth
                )
            }
        }

        // Кнопка внизу рамки
        Button(
            onClick = {
                if (scannedBarcode != null) {
                    onBarcodeConfirmed(scannedBarcode)
                } else {
                    Toast.makeText(context, "Наведите камеру на штрихкод", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (rectHeight / 2) + 32.dp) // смещаем кнопку вниз от центра рамки
        ) {
            Text(text = "Сканировать")
        }
    }
}