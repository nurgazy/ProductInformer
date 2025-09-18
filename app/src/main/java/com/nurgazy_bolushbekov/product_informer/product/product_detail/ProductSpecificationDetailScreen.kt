package com.nurgazy_bolushbekov.product_informer.product.product_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.nurgazy_bolushbekov.product_informer.product.entity.SpecificationWithProduct
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem


@Composable
fun ProductSpecificationDetailScreen(
    navController: NavHostController,
    productId: String,
    vm: ProductSpecificationDetailViewModel = hiltViewModel()
) {

    val tabs = ProductDetailTab.entries.toTypedArray()
    val pagerState = rememberPagerState (pageCount = { tabs.size })
    val productWithSpec by vm.specWithProduct.collectAsState()

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
                ProductDetailTab.PRODUCT_DETAIL -> SpecificationDetailScreenContent(productWithSpec!!)
                ProductDetailTab.PRODUCT_IMAGE -> SpecificationImageScreenContent(productWithSpec!!)
            }
        }
        Spacer(Modifier.height(16.dp))
        PagerIndicator(pagerState = pagerState, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun SpecificationDetailScreenContent(productSpecification: SpecificationWithProduct) {

    val product = productSpecification.product

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
                items(listOf(productSpecification.specification)){ curProductSpec ->
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

@Composable
fun SpecificationImageScreenContent(productSpecification: SpecificationWithProduct) {

    val context = LocalContext.current
    val product = productSpecification.product

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