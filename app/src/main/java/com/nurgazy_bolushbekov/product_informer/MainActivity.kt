package com.nurgazy_bolushbekov.product_informer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nurgazy_bolushbekov.product_informer.main_menu_page.MainMenuScreen
import com.nurgazy_bolushbekov.product_informer.price_checker.PriceCheckerScreen
import com.nurgazy_bolushbekov.product_informer.settings_page.SettingScreen
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(){

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ScreenNavItem.Settings.route) {
        composable(ScreenNavItem.Settings.route) { SettingScreen(navController) }
        composable(ScreenNavItem.MainMenu.route) { MainMenuScreen(navController) }
        composable(ScreenNavItem.PriceChecker.route) { PriceCheckerScreen(navController) }
    }
}


