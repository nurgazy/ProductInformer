package com.nurgazy_bolushbekov.product_informer.main_menu_page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainMenuPage(navController: NavController){
    Column(Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        ) {
            Text(text = "Чек прайс", fontSize = 25.sp)
        }
    }
}