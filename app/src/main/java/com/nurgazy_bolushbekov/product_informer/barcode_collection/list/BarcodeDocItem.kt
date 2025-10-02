package com.nurgazy_bolushbekov.product_informer.barcode_collection.list

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc

@Composable
fun BarcodeDocItem(
    document: BarcodeDoc,
    onEditClick: (BarcodeDoc) -> Unit,
    onDeleteClick: (BarcodeDoc) -> Unit// Лямбда-функция для обработки клика
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        // Используем ElevatedCard для красивой тени (Material 3)
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // --- Секция Номер и Статус ---
            Column(
                modifier = Modifier.weight(1f) // Занимает большую часть ширины
            ) {
                // Номер документа
                Text(
                    text = document.barcodeDocId.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Статус документа
                Text(
                    text = "Статус: ${document.status}",
                    style = MaterialTheme.typography.bodySmall,
                    // Добавим цвет в зависимости от статуса для красоты
//                    color = when (document.status) {
//                        "Завершен" -> Color(0xFF4CAF50) // Зеленый
//                        "Отклонен" -> Color(0xFFF44336) // Красный
//                        else -> MaterialTheme.colorScheme.secondary // Другие статусы
//                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- Секция Кнопка Редактировать ---
            IconButton(
                onClick = { onEditClick(document) },
                enabled = true,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Редактировать документ ${document.barcodeDocId}",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

            // 2. Кнопка Удалить <-- НОВЫЙ ЭЛЕМЕНТ
            IconButton(
                onClick = { onDeleteClick(document) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete, // Иконка "Корзина"
                    contentDescription = "Удалить документ ${document.barcodeDocId}",
                    tint = MaterialTheme.colorScheme.error // Используем цвет ошибки (красный)
                )
            }
        }
    }
}