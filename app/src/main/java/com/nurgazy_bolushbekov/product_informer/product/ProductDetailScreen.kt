package com.nurgazy_bolushbekov.product_informer.product

import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.nurgazy_bolushbekov.product_informer.data_classes.Product
import kotlinx.coroutines.launch
import java.io.File


enum class ProductDetailTab(val title: String) {
    PRODUCT_DETAIL("Детали"),
    PRODUCT_IMAGE("Картинка"),
}

@Composable
fun ProductDetailScreen(product: Product?) {


    val tabs = ProductDetailTab.entries.toTypedArray()
    val pagerState = rememberPagerState (pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    var selectedOption by remember { mutableStateOf(tabs[0]) }

    if (product == null) {
        Column(Modifier.fillMaxWidth()) {
            Text(text = "Нет данных")
        }
        return
    }

    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxSize()
    ) {
//        SingleChoiceSegmentedButtonRow(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            tabs.forEachIndexed { index, tab ->
//                SegmentedButton(
//                    selected = pagerState.currentPage == index,
//                    onClick = { coroutineScope.launch {
//                        selectedOption = tabs[index]
//                        pagerState.animateScrollToPage(index)
//                    } },
//                    shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size)
//                ) {
//                    Text(tab.title)
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // 2. Условное отображение контента
//        when (selectedOption) {
//            ProductDetailTab.PRODUCT_DETAIL -> DetailScreenContent(product)
//            ProductDetailTab.PRODUCT_IMAGE -> ImageScreenContent(product)
//        }

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ){
            tabs.forEachIndexed { index, tab ->
                Tab(selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(tab.title) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (tabs[page]) {
                ProductDetailTab.PRODUCT_DETAIL -> DetailScreenContent(product)
                ProductDetailTab.PRODUCT_IMAGE -> ImageScreenContent(product)
            }
        }

    }


    }

@Composable
fun DetailScreenContent(product: Product) {
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

        HorizontalDivider()

        Column{
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(listOf(product.productSpecifications)){ item ->
                    item?.forEach{ curProductSpec ->
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

                                    if (curBalance.cellStock != null){
                                        CollapsibleItem(title = "Ячейки"){
                                            curBalance.cellStock.forEach{ curCellStock ->
                                                Row(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .padding(5.dp),
                                                ) {
                                                    Text(
                                                        text = curCellStock.cell,
                                                        textAlign = TextAlign.Start,
                                                        modifier = Modifier.weight(1f)
                                                    )

                                                    Text(
                                                        text = "${curCellStock.inStock}",
                                                        textAlign = TextAlign.End,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
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

    }

}

@Composable
fun ImageScreenContent(product: Product) {

    val context = LocalContext.current

    val imageFile = getCachedImageFile(context, "${product.article}.jpeg")
    if (imageFile != null) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageFile)
                .crossfade(true)
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = product.barcode,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    } else {
        Text("Файл изображения не найден в кэше.")
    }

}

@Composable
fun CollapsibleItem(
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 5.dp)) {
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

fun getCachedImageFile(context: Context, fileName: String): File? {
    val cacheDir = context.cacheDir
    val file = File(cacheDir, fileName)
    return if (file.exists()) file else null
}