// Добавьте эти импорты в BarcodeScannerScreen.kt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = 3.dp.toPx()

        // Ширина на весь экран, высота — 30% от высоты экрана
        val rectWidth = size.width
        val rectHeight = size.height * 0.3f

        val left = 0f
        val top = (size.height - rectHeight) / 2
        val rect = Rect(Offset(left, top), Size(rectWidth, rectHeight))

        drawContext.canvas.nativeCanvas.apply {
            val checkpoint = saveLayer(null, null)
            drawRect(Color.Black.copy(alpha = 0.6f))

            // Прозрачное окно во всю ширину
            drawRect(
                color = Color.Transparent,
                topLeft = rect.topLeft,
                size = rect.size,
                blendMode = BlendMode.Clear
            )
            restoreToCount(checkpoint)
        }

        // Рисуем только верхнюю и нижнюю границы, так как боковые за краем
        drawLine(
            color = Color.White,
            start = Offset(0f, top),
            end = Offset(size.width, top),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.White,
            start = Offset(0f, top + rectHeight),
            end = Offset(size.width, top + rectHeight),
            strokeWidth = strokeWidth
        )
    }
}