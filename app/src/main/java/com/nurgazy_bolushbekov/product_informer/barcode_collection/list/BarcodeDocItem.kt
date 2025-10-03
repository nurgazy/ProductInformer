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
    onDeleteClick: (BarcodeDoc) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = document.barcodeDocId.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Статус: ${document.status}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

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