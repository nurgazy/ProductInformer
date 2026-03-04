package com.nurgazy_bolushbekov.product_informer.barcode_input

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nurgazy_bolushbekov.product_informer.barcode_scanner.ScannerActivity
import com.nurgazy_bolushbekov.product_informer.data_classes.ProductResponse
import com.nurgazy_bolushbekov.product_informer.product.SharedVM
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem

@Composable
fun BarcodeInputScreen(
    navController: NavController,
    vm: BarcodeInputViewModel = hiltViewModel(),
    sharedVM: SharedVM
){
    val context = LocalContext.current
    val barcodeText by vm.barcode.collectAsState()
    val productResponse by vm.productResponse.collectAsState()
    val navigateDetailScreen by vm.navigateDetailScreen.collectAsState()
    val alertText by vm.alertText.collectAsState()
    val showDialog by vm.showDialog.collectAsState()

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedBarcode = result.data?.getStringExtra("BARCODE_RESULT")
            if (scannedBarcode != null) {
                vm.onChangeBarcode(scannedBarcode)
                vm.refreshProduct() // Автоматический поиск после сканирования
            }
        }
    }

    BackHandler(enabled = true) {  }

    val onSetProductSharedVM: (productData: ProductResponse?) -> Unit = sharedVM::onSetProductData
    LaunchedEffect(Unit) {
        vm.setPreviousRoute(sharedVM.previousRoute.value)
    }

    LaunchedEffect(navigateDetailScreen) {
        if (navigateDetailScreen) {
            val product = (productResponse as? ResultFetchData.Success)?.data
            if (product != null) {
                onSetProductSharedVM(product)
                val route = vm.prevRoute.value ?: ScreenNavItem.ProductDetail.route
                navController.navigate(route)
                vm.resetNavigationDetailScreen()
                vm.resetPreviousRoute()
            }
        }
    }

    ProductInformationContent(
        barcodeText = barcodeText,
        productResponse = productResponse,
        onSearchClick = { vm.refreshProduct() },
        onScanClick = {
            val intent = Intent(context, ScannerActivity::class.java)
            scannerLauncher.launch(intent)
        },
        showDialog = showDialog,
        alertText = alertText,
        vm = vm
    )
}

@Composable
fun ProductInformationContent(
    barcodeText: String,
    productResponse: ResultFetchData<ProductResponse>?,
    onSearchClick: () -> Unit,
    onScanClick: () -> Unit, // Новый параметр
    showDialog: Boolean,
    alertText: String,
    vm: BarcodeInputViewModel
) {

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
            placeholder = { Text("Введите штрихкод", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF2F2F7),
                unfocusedContainerColor = Color(0xFFF2F2F7),
                disabledContainerColor = Color(0xFFF2F2F7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Button(
                onClick = onScanClick, // Вызываем запуск активити
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_camera), // Замените на иконку сканера
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Сканировать", fontSize = 16.sp)
            }
            Button(
                onClick = onSearchClick,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Найти", fontSize = 16.sp)
            }
        }

        when(productResponse){
            null -> {}
            is ResultFetchData.Error -> {
                val error = (productResponse as ResultFetchData.Error).exception
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