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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nurgazy_bolushbekov.product_informer.ScannerActivity
import com.nurgazy_bolushbekov.product_informer.product.SharedVM
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem

private const val BARCODE_RESULT_KEY = "BARCODE_RESULT"

@Composable
fun BarcodeDetailScreen(
    navController: NavHostController,
    sharedVM: SharedVM,
    vm: BarcodeDetailVM = hiltViewModel(),
    barcodeDocId: Long
) {
    val barcodeDoc by vm.curBarcodeDoc.collectAsState()
    val barcodeList by vm.barcodeList.collectAsState()
    val productData by sharedVM.productData.collectAsState()

    LaunchedEffect(Unit) {
        vm.setBarcodeDoc(barcodeDocId)
        vm.addToBarcodeList(productData)
    }

    DisposableEffect(Unit) {
        onDispose {
            sharedVM.resetProductData()
        }
    }

    BackHandler {
        navController.popBackStack(ScreenNavItem.BarcodeList.route, inclusive = false)
    }

    val context = LocalContext.current

    // 1. РЕГИСТРАЦИЯ LAUNCHER
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedBarcode = result.data?.getStringExtra(BARCODE_RESULT_KEY)

            if (scannedBarcode != null) {
                Log.d("ProductInformer", "Scanned barcode: $scannedBarcode")
                Log.d("ProductInformer", "barcodeDoc: $barcodeDoc")
                vm.refreshProduct(scannedBarcode)
            }
        } else {
            Log.d("ProductInformer", "Сканирование отменено.")
        }
    }

    Log.d("ProductInformer", "barcodeList: $barcodeList")

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (barcodeDoc == null)
            Text(text = "Документ (Новый)")
        else
            Text(text = "Документ №${barcodeDoc!!.barcodeDocId}")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(barcodeList) { item ->
                BarcodeDetailListItem(
                    item = item,
                    onDeleteClick = { itemForDelete ->
                        vm.removeItemFromList(itemForDelete)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                vm.saveCurBarcodeDoc()
            }) {
                Text("Сохранить")
            }
            Button(onClick = {
                val intent = Intent(context, ScannerActivity::class.java)
                scannerLauncher.launch(intent)
            }) {
                Text("Сканировать")
            }
        }
    }

}