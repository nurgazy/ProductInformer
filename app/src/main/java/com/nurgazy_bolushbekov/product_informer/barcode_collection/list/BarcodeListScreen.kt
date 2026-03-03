package com.nurgazy_bolushbekov.product_informer.barcode_collection.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem

@Composable
fun BarcodeListScreen(
    navController: NavHostController,
    vm: BarcodeListVM = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        vm.refreshBarcodeDocList()
    }
    val barcodeDocs by vm.barcodeDocList.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(barcodeDocs){ barcodeDoc ->
                BarcodeDocItem(
                    document = barcodeDoc,
                    onEditClick = {navController.navigate(ScreenNavItem.BarcodeDetail.route+"?barcodeDocId=${barcodeDoc.barcodeDocId}")},
                    onDeleteClick = { vm.onDeleteDoc(barcodeDoc) }
                )
            }
        }

        Button(
            onClick = {
                navController.navigate(ScreenNavItem.BarcodeDetail.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF),
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = "Добавить",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }

}