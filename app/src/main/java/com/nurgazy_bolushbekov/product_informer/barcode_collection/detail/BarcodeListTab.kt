package com.nurgazy_bolushbekov.product_informer.barcode_collection.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nurgazy_bolushbekov.product_informer.utils.BarcodeStatus

@Composable
fun BarcodeListTab(vm: BarcodeDetailVM) {

    val barcodeDoc by vm.curBarcodeDoc.collectAsState()
    val curBarcodeData by vm.curBarcodeData.collectAsState()
    val showQuantityInputDialog by vm.showQuantityInputDialog.collectAsState()
    val uploadStatusMessage by vm.uploadStatusMessage.collectAsState()

    val filteredBarcodeList by vm.filteredBarcodeList.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()
    val isUploaded = barcodeDoc?.status == BarcodeStatus.UPLOADED

    val showSpecSelectionDialog by vm.showSpecSelectionDialog.collectAsState()
    val availableSpecs by vm.availableSpecs.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = barcodeDoc?.let { "Документ №${it.barcodeDocId}" } ?: "Новый документ",
            style = MaterialTheme.typography.headlineMedium,
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

    if (showSpecSelectionDialog) {
        SpecSelectionDialog(
            specs = availableSpecs,
            onSpecSelected = { spec -> vm.onSpecSelected(spec) },
            onDismiss = { vm.dismissSpecSelection() }
        )
    }
}