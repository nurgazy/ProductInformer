package com.nurgazy_bolushbekov.product_informer.barcode_collection.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductSpecificResponse

@Composable
fun SpecSelectionDialog(
    specs: List<ProductSpecificResponse>,
    onSpecSelected: (ProductSpecificResponse) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите характеристику") },
        text = {
            LazyColumn {
                items(specs) { spec ->
                    TextButton(
                        onClick = { onSpecSelected(spec) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = spec.name, style = MaterialTheme.typography.bodyLarge)
                    }

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}