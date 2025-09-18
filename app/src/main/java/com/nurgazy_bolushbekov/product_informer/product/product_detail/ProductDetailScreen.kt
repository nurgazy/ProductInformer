package com.nurgazy_bolushbekov.product_informer.product.product_detail

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.nurgazy_bolushbekov.product_informer.product.entity.ProductWithSpecifications
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem
import java.io.File


enum class ProductDetailTab {
    PRODUCT_DETAIL,
    PRODUCT_IMAGE,
}

@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: String,
    vm: ProductDetailViewModel = hiltViewModel()
) {

    val tabs = ProductDetailTab.entries.toTypedArray()
    val pagerState = rememberPagerState (pageCount = { tabs.size })
    val productWithSpec by vm.productWithSpecifications.collectAsState()

    LaunchedEffect(true) {
        vm.getProductFromDB(productId)
    }

    BackHandler {
//        vm.deleteImageFromCache()
        navController.popBackStack(ScreenNavItem.SearchProductInfo.route, false)
    }

    if (productWithSpec == null) {
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
        ) { page ->
            when (tabs[page]) {
                ProductDetailTab.PRODUCT_DETAIL -> DetailScreenContent(productWithSpec!!)
                ProductDetailTab.PRODUCT_IMAGE -> ImageScreenContent(productWithSpec!!)
            }
        }
        Spacer(Modifier.height(16.dp))
        PagerIndicator(pagerState = pagerState, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(CircleShape)
                    .size(8.dp)
                    .background(color)
            )
        }
    }
}

@Composable
fun DetailScreenContent(productWithSpecifications: ProductWithSpecifications) {

    val product = productWithSpecifications.product

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
                text = product.productName,
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
                items(listOf(productWithSpecifications.specifications)){ item ->
                    item.forEach{ curProductSpec ->
                        CollapsibleSpecificatonItem(title = curProductSpec.name) {
                            CollapsibleItem(title = "Остатки (В наличии/Доступно)") {
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
fun ImageScreenContent(productWithSpecifications: ProductWithSpecifications) {

    val context = LocalContext.current
    val product = productWithSpecifications.product

    val imageFile = getCachedImageFile(context, "${product.productUuid1C}.jpeg")
    if (imageFile != null) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageFile)
                .crossfade(true)
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = product.productName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    } else {
        Text("Файл изображения не найден.")
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

@Composable
fun CollapsibleSpecificatonItem(
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
                .background(Color(0xFF83F583))
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