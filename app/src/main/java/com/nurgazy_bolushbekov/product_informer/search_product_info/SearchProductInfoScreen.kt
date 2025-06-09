package com.nurgazy_bolushbekov.product_informer.search_product_info

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SearchProductInfoScreen(navController: NavController){

    val vm: SearchProductInfoViewModel = viewModel(
        factory = SearchProductInfoViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    BarcodeScannerScreen(vm, navController)
}

@Composable
fun ProductInformationContent(
    vm: SearchProductInfoViewModel,
    isScannerVisible: MutableState<Boolean>,
    navController: NavController
) {

    val barcodeText by vm.barcode.collectAsState()
    val productResult by vm.product.collectAsState()
    val navigateDetailScreen by vm.navigateDetailScreen.collectAsState()

    LaunchedEffect(navigateDetailScreen) {
        if (navigateDetailScreen) {
            val product = (productResult as ResultFetchData.Success).data
            navController.navigate(ScreenNavItem.ProductDetail.route+"/${Json.encodeToString(product)}")
            vm.resetNavigationDetailScreen() // Сбрасываем флаг после навигации
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = barcodeText,
            onValueChange = { vm.onChangeBarcode(it) },
            singleLine = true,
            modifier = Modifier
                .padding(5.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                onClick = { isScannerVisible.value = true },
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
            ) {
                Text("Сканировать")
            }
            Button(
                onClick = { vm.getInfo() },
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
            ) {
                Text("Найти")
            }
        }

        when(productResult){
            null -> {}
            is ResultFetchData.Error -> {
                val error = (productResult as ResultFetchData.Error).exception
                Text(text = "Ошибка: ${error.message}")
            }
            ResultFetchData.Loading -> {
                Box {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            is ResultFetchData.Success -> {}
        }
    }
}