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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val tabTitles = listOf("Товары", "Доп. инфо")

    val barcodeDoc by vm.curBarcodeDoc.collectAsState()
    val isUploaded = barcodeDoc?.status == BarcodeStatus.UPLOADED
    val triggerScanner by vm.triggerScanner.collectAsState()

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
                    onClick = { vm.uploadTo1C() },
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
                        text = { Text(title) }
                    )
                }
            }
        }
    }

}

