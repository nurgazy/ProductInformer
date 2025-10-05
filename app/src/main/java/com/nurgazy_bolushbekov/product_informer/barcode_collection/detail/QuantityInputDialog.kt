package com.nurgazy_bolushbekov.product_informer.barcode_collection.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun QuantityInputDialog(
    barcode: String,
    onQuantityConfirmed: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var quantityText by remember { mutableStateOf("1") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Введите количество") },
        text = {
            Column {
                Text(text = "Штрихкод: $barcode", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { newValue ->
                        // Разрешаем только цифры
                        if (newValue.all { it.isDigit() }) {
                            quantityText = newValue.trimStart('0')
                            isError = false
                        }
                    },
                    label = { Text("Количество") },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isError) {
                    Text(
                        text = "Введите корректное число больше 0.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val quantity = quantityText.toIntOrNull()
                    if (quantity != null && quantity > 0) {
                        onQuantityConfirmed(quantity)
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}