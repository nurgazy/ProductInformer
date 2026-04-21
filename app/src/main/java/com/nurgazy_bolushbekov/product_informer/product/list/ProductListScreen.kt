package com.nurgazy_bolushbekov.product_informer.product.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun ProductListScreen(
    navController: NavHostController,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(products) { product ->
                ListItem(
                    headlineContent = { Text(product.productName) },
                    supportingContent = {
                        Column {
                            Text(text = "Артикул: ${product.article}")
                            Text(text = "Штрихкод: ${product.barcode}")
                        }
                    }
                )
            }
        }
    }
}