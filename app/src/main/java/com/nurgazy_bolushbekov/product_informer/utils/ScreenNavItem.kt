package com.nurgazy_bolushbekov.product_informer.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Settings : ScreenNavItem("Settings", "Настройки", Icons.Filled.Settings)
    data object MainMenu : ScreenNavItem("MainMenu","Главное меню", Icons.Filled.Menu)
    data object SearchProductInfo : ScreenNavItem("SearchProductInfo","Поиск товара", Icons.Filled.Search)
    data object ProductDetail: ScreenNavItem("ProductDetail","Детали товара", Icons.Filled.Info)
    data object BarcodeList: ScreenNavItem("BarcodeList","Сбор штрихкодов", Icons.Filled.ShoppingCart)
    data object BarcodeDetail: ScreenNavItem("BarcodeDetail","Данные штрихкодов", Icons.Filled.List)
}