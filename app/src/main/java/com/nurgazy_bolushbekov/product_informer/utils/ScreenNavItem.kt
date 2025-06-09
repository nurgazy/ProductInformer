package com.nurgazy_bolushbekov.product_informer.utils

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenNavItem(val route: String, val title: String) {

    data object Settings : ScreenNavItem("Settings", "Настройки")
    data object MainMenu : ScreenNavItem("MainMenu","Главное меню")
    data object SearchProductInfo : ScreenNavItem("SearchProductInfo","Поиск товара")
    data object ProductDetail: ScreenNavItem("ProductDetail/{productJson}","Детали товара")
}