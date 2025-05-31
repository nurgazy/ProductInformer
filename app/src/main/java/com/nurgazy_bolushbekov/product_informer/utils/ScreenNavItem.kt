package com.nurgazy_bolushbekov.product_informer.utils

sealed class ScreenNavItem(val route: String) {
    data object Settings : ScreenNavItem("settings")
    data object MainMenu : ScreenNavItem("main_menu")
    data object PriceChecker : ScreenNavItem("price_checker")
    data object ProductDetail: ScreenNavItem("product_detail")
}