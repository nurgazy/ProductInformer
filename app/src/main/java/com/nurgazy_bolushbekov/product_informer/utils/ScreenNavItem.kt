package com.nurgazy_bolushbekov.product_informer.utils

sealed class ScreenNavItem(val route: String) {
    data object Settings : ScreenNavItem("Настройки")
    data object MainMenu : ScreenNavItem("Главное меню")
    data object ProductInformation : ScreenNavItem("Поиск товара")
    data object ProductDetail: ScreenNavItem("Детали товара")
}