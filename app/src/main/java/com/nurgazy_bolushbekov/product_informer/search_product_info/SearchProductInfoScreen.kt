package com.nurgazy_bolushbekov.product_informer.search_product_info

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem

@Composable
fun SearchProductInfoScreen(
    navController: NavController,
    vm: SearchProductInfoViewModel = hiltViewModel()
){
    BarcodeScannerScreen(vm, navController)
}

@Composable
fun ProductInformationContent(
    vm: SearchProductInfoViewModel,
    isScannerVisible: MutableState<Boolean>,
    navController: NavController
) {

    val barcodeText by vm.barcode.collectAsState()
    val refreshResult by vm.refreshResult.collectAsState()
    val navigateDetailScreen by vm.navigateDetailScreen.collectAsState()
    val showDialog by vm.showDialog.collectAsState()
    val alertText by vm.alertText.collectAsState()
    val serverUrl by vm.serverUrl.collectAsState()
    val productId by vm.productId.collectAsState()
    val isFullSpecifications by vm.isFullSpecifications.collectAsState()

    Log.d("ProductInformer", "isFullSpecifications: $isFullSpecifications")

    LaunchedEffect(navigateDetailScreen) {
        if (navigateDetailScreen) {
            navController.navigate(ScreenNavItem.ProductDetail.route+"/$productId")
            vm.resetNavigationDetailScreen()
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
                onClick = {
                    if (serverUrl.isEmpty()){
                        navController.navigate(ScreenNavItem.ProductDetail.route+"/$productId")
                    }else {
                        isScannerVisible.value = true
                    }
                },
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
            ) {
                Text("Сканировать")
            }
            Button(
                onClick = {
                    if (serverUrl.isEmpty()){
                        navController.navigate(ScreenNavItem.ProductDetail.route+"/$productId")
                    }else {
                        vm.refreshProduct()
                    }
                },
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
            ) {
                Text("Найти")
            }
        }

        when(refreshResult){
            null -> {}
            is ResultFetchData.Error -> {
                val error = (refreshResult as ResultFetchData.Error).exception
                ExpandableText(
                    text = "Ошибка: ${error.message}"
                )
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

    if (showDialog){
        AlertDialog(
            onDismissRequest = { vm.resetShowAlertDialog() },
            text = { Text(text = alertText) },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetShowAlertDialog()
                }) {
                    Text("OK")
                }
            }
        )
    }
}


@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    minLines: Int = 3
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable { expanded = !expanded },
            maxLines = if (expanded) Int.MAX_VALUE else minLines,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )

        if (!expanded) {
            Text(
                text = "Показать больше",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.End)
                    .clickable { expanded = true }
            )
        } else {
            Text(
                text = "Свернуть",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.End)
                    .clickable { expanded = false }
            )
        }
    }
}