package com.nurgazy_bolushbekov.product_informer.product

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import kotlinx.serialization.json.Json

@Composable
fun ProductDetailScreen(productJson: String) {
    Log.d("ProductInformer", "ProductDetailScreen is called")
    Log.d("ProductInformer", productJson)
    val product: Product = remember { Json.decodeFromString<Product>(productJson) }

    Column(Modifier.fillMaxWidth()) {
        //Наименование
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider()

        //Штрихкод
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
        ) {
            Text(
                text = "Штрихкод",
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = product.barcode,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }

        //Артикул
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
        ) {
            Text(
                text = "Артикул",
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = product.article,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }

        //Производитель
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
        ) {
            Text(
                text = "Производитель",
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = product.manufacturer,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }

        //Марка
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
        ) {
            Text(
                text = "Марка",
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = product.brand,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }

        //Товарная категория
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
        ) {
            Text(
                text = "Товарная категория",
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = product.productCategory,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }

        Row {

        }

    }
}