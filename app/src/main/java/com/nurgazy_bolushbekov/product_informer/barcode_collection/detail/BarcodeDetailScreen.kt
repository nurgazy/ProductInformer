package com.nurgazy_bolushbekov.product_informer.barcode_collection.detail

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nurgazy_bolushbekov.product_informer.barcode_scanner.ScannerActivity
import com.nurgazy_bolushbekov.product_informer.utils.BarcodeStatus
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem
import kotlinx.coroutines.launch

private const val BARCODE_RESULT_KEY = "BARCODE_RESULT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeDetailScreen(
    navController: NavHostController,
    vm: BarcodeDetailVM = hiltViewModel(),
    barcodeDocId: Long
) {
    LaunchedEffect(Unit) {
        vm.setBarcodeDoc(barcodeDocId)
    }

    BackHandler {
        navController.popBackStack(ScreenNavItem.BarcodeList.route, inclusive = false)
    }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val tabTitles = listOf(
        "Товары" to Icons.Default.ShoppingCart,
        "Доп. инфо" to Icons.Default.Info
    )

    val barcodeDoc by vm.curBarcodeDoc.collectAsState()
    val isUploaded = barcodeDoc?.status == BarcodeStatus.UPLOADED
    val triggerScanner by vm.triggerScanner.collectAsState()

    var showManualInputDialog by remember { mutableStateOf(false) }
    var manualBarcode by remember { mutableStateOf("") }

    val context = LocalContext.current

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

    LaunchedEffect(triggerScanner) {
        if (triggerScanner) {
            val intent = Intent(context, ScannerActivity::class.java)
            scannerLauncher.launch(intent)
            vm.resetScannerTrigger()
        }
    }

    Scaffold(
        floatingActionButton = {
            // Показываем FAB только на вкладке "Товары" (индекс 0)
            if (pagerState.currentPage == 0 && !isUploaded) {
                FloatingActionButton(
                    onClick = {
                        manualBarcode = ""
                        showManualInputDialog = true
                    },
                    modifier = Modifier.padding(bottom = 30.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ввести вручную")
                }
            }
        },
        bottomBar = {
            // Выносим кнопки в нижнюю панель
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(
                        onClick = { vm.saveCurBarcodeDoc() },
                        modifier = Modifier.weight(1f),
                        enabled = !isUploaded
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Сохранить")
                    }

                    ElevatedButton(
                        onClick = {
                            val intent = Intent(context, ScannerActivity::class.java)
                            scannerLauncher.launch(intent)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isUploaded
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Сканер")
                    }
                }

                FilledTonalButton(
                    onClick = { vm.uploadData() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploaded
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Выгрузить")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> BarcodeListTab(vm)
                    1 -> BarcodeAdditionalInfoTab(vm)
                }
            }

            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(
                            title.first,
                            style = MaterialTheme.typography.labelSmall
                        ) },
                        icon = {
                            Icon(
                                imageVector = title.second,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }

    if (showManualInputDialog) {
        AlertDialog(
            onDismissRequest = { showManualInputDialog = false },
            title = { Text("Ввод штрихкода") },
            text = {
                OutlinedTextField(
                    value = manualBarcode,
                    onValueChange = { manualBarcode = it },
                    label = { Text("Штрихкод") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (manualBarcode.isNotBlank()) {
                            vm.refreshProduct(manualBarcode, isManual = true)
                            showManualInputDialog = false
                        }
                    }
                ) {
                    Text("ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualInputDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

}

