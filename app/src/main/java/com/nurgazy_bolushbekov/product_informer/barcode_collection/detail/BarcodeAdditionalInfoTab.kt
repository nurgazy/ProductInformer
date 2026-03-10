package com.nurgazy_bolushbekov.product_informer.barcode_collection.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nurgazy_bolushbekov.product_informer.utils.BarcodeStatus

@Composable
fun BarcodeAdditionalInfoTab(vm: BarcodeDetailVM) {
    val comment by vm.comment.collectAsState()
    val curBarcodeDoc by vm.curBarcodeDoc.collectAsState()
    val isUploaded = curBarcodeDoc?.status == BarcodeStatus.UPLOADED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = comment,
            onValueChange = { vm.onCommentChange(it) },
            label = { Text("Комментарий") },
            modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
            enabled = !isUploaded,
            minLines = 3,
            maxLines = 5,
        )
    }
}