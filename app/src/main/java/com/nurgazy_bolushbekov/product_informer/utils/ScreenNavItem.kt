package com.nurgazy_bolushbekov.product_informer.utils

sealed class ScreenNavItem(val route: String) {
    object Settings : ScreenNavItem("settings")
    object MainMenu : ScreenNavItem("main_menu")
}