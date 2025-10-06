package com.nurgazy_bolushbekov.product_informer.barcode_collection.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc
import com.nurgazy_bolushbekov.product_informer.utils.BarcodeStatus

@Composable
fun BarcodeDocItem(
    document: BarcodeDoc,
    onEditClick: (BarcodeDoc) -> Unit,
    onDeleteClick: (BarcodeDoc) -> Unit
) {

    var docStatus by rememberSaveable { mutableStateOf("") }
    docStatus = when(document.status){
        BarcodeStatus.ACTIVE -> "Активный"
        BarcodeStatus.COMPLETED -> "Завершен"
    }

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

            Row(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "${document.barcodeDocId}. ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Статус: $docStatus",
                    style = MaterialTheme.typography.titleMedium,
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