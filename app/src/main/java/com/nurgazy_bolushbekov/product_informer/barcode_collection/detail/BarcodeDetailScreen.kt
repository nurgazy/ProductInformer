package com.nurgazy_bolushbekov.product_informer.barcode_collection.detail

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nurgazy_bolushbekov.product_informer.barcode_scanner.ScannerActivity
import com.nurgazy_bolushbekov.product_informer.utils.BarcodeStatus
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem

private const val BARCODE_RESULT_KEY = "BARCODE_RESULT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeDetailScreen(
    navController: NavHostController,
    vm: BarcodeDetailVM = hiltViewModel(),
    barcodeDocId: Long
) {
    val barcodeDoc by vm.curBarcodeDoc.collectAsState()
    val barcodeList by vm.barcodeList.collectAsState()
    val curBarcodeData by vm.curBarcodeData.collectAsState()
    val showQuantityInputDialog by vm.showQuantityInputDialog.collectAsState()
    val uploadStatusMessage by vm.uploadStatusMessage.collectAsState()

    val filteredBarcodeList by vm.filteredBarcodeList.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        vm.setBarcodeDoc(barcodeDocId)

    }

    BackHandler {
        navController.popBackStack(ScreenNavItem.BarcodeList.route, inclusive = false)
    }

    val context = LocalContext.current

    val isUploaded = barcodeDoc?.status == BarcodeStatus.UPLOADED

    // 1. РЕГИСТРАЦИЯ LAUNCHER
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedBarcode = result.data?.getStringExtra(BARCODE_RESULT_KEY)
            if (scannedBarcode != null) {
                vm.refreshProduct(scannedBarcode)
            }
        } else {
            Log.d("ProductInformer", "Сканирование отменено.")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = if (barcodeDoc == null)
                "Новый документ"
            else
                "Документ №${barcodeDoc!!.barcodeDocId}",
            style = MaterialTheme.typography.headlineMedium, // Использована более крупная типографика
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = vm::onSearchQueryChanged,
            label = { Text("Поиск штрихкода или товара") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(filteredBarcodeList) { index, item ->
                BarcodeDetailListItem(
                    itemNumber = index + 1,
                    item = item,
                    onDeleteClick = { itemForDelete ->
                        if (!isUploaded)
                            vm.removeFromBarcodeList(itemForDelete)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ElevatedButton(
                onClick = { vm.saveCurBarcodeDoc() },
                modifier = Modifier.weight(1f),
                enabled = !isUploaded
            ) {
                Icon(Icons.Filled.Save, contentDescription = "Сохранить")
                Spacer(Modifier.width(8.dp))
                Text("Сохранить")
            }

            OutlinedButton(
                onClick = {
                    val intent = Intent(context, ScannerActivity::class.java)
                    scannerLauncher.launch(intent)
                },
                modifier = Modifier.weight(1f),
                enabled = !isUploaded
            ) {
                Icon(Icons.Filled.ViewAgenda, contentDescription = "Сканировать")
                Spacer(Modifier.width(8.dp))
                Text("Сканировать")
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Разделитель между рядами кнопок

        FilledTonalButton(
            onClick = {
                vm.uploadTo1C()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploaded
        ) {
            Icon(Icons.Filled.Upload, contentDescription = "Выгрузить в 1С")
            Spacer(Modifier.width(8.dp))
            Text("Выгрузить в 1С")
        }
    }

    if (uploadStatusMessage != null) {
        AlertDialog(
            onDismissRequest = {
                vm.clearUploadStatusMessage()
            },
            title = {
                Text("Информация об выгрузке!!!")
            },
            text = {
                Text(uploadStatusMessage ?: "")
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.clearUploadStatusMessage()
                    }
                ) {
                    Text("ОК")
                }
            }
        )
    }

    if (showQuantityInputDialog) {
        QuantityInputDialog(
            barcode = curBarcodeData!!.barcode,
            onQuantityConfirmed = { quantity ->
                vm.addToBarcodeList(quantity)
                vm.resetShowQuantityInputDialog()
            },
            onDismiss = {
                vm.resetShowQuantityInputDialog()
            }
        )
    }

}