package com.nurgazy_bolushbekov.product_informer.product

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import kotlinx.serialization.json.Json

@Composable
fun ProductDetailScreen(productJson: String) {
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

        Column{
            product.productSpecifications?.forEach{ curProductSpec ->
                CollapsibleItem(title = curProductSpec.name) {
                    CollapsibleItem(title = "Остатки (В наличии/Доступно)") {
                        curProductSpec.balance?.forEach { curBalance ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                            ) {
                                Text(
                                    text = curBalance.warehouse,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${curBalance.inStock} / ${curBalance.available} ${curBalance.unit}",
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    CollapsibleItem(title = "Цены") {
                        curProductSpec.price?.forEach{ curPrice ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                            ) {
                                Text(
                                    text = curPrice.priceType,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${curPrice.price}  ${curPrice.currency}",
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

        }

    }
}

@Composable
fun CollapsibleItem(
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clickable { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть"
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically(animationSpec = tween(durationMillis = 300)),
            exit = slideOutVertically(animationSpec = tween(durationMillis = 300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                content()
            }
        }
    }
}