package com.nurgazy_bolushbekov.product_informer.product

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import kotlinx.serialization.json.Json

@Composable
fun ProductDetailScreen(productJson: String) {
    Log.d("ProductInformer", "ProductDetailScreen is called")
    Log.d("ProductInformer", productJson)
    val product: Product = remember { Json.decodeFromString<Product>(productJson) }

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold
            )
        }

    }
}