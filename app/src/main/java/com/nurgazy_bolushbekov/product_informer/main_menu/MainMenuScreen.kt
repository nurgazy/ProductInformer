package com.nurgazy_bolushbekov.product_informer.main_menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem

@Composable
fun MainMenuScreen(navController: NavController){

    Column(
        modifier =Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate(ScreenNavItem.SearchProductInfo.route) }
        ) {
            Text(text = "Поиск товара", fontSize = 25.sp)
        }
    }
}