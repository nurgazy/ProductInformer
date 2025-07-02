package com.nurgazy_bolushbekov.product_informer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nurgazy_bolushbekov.product_informer.main_menu.MainMenuScreen
import com.nurgazy_bolushbekov.product_informer.product.ProductDetailScreen
import com.nurgazy_bolushbekov.product_informer.product.ProductSharedViewModel
import com.nurgazy_bolushbekov.product_informer.search_product_info.SearchProductInfoScreen
import com.nurgazy_bolushbekov.product_informer.settings.SettingScreen
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SideNavigationMenu()
        }
    }
}

@Composable
fun MainScreen(paddingValues: PaddingValues, navController: NavHostController){

    NavHost(
        navController = navController,
        startDestination = ScreenNavItem.SearchProductInfo.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(ScreenNavItem.Settings.route) { SettingScreen(navController) }
        composable(ScreenNavItem.MainMenu.route) { MainMenuScreen(navController) }
        composable(ScreenNavItem.SearchProductInfo.route) {
            val sharedViewModel: ProductSharedViewModel = viewModel(navController.getBackStackEntry(ScreenNavItem.SearchProductInfo.route))
            SearchProductInfoScreen(navController, sharedViewModel)
        }
        composable(ScreenNavItem.ProductDetail.route) {
            val sharedViewModel: ProductSharedViewModel = viewModel(navController.getBackStackEntry(ScreenNavItem.SearchProductInfo.route))
            val productData by sharedViewModel.currentProduct.collectAsState()
            ProductDetailScreen(productData)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SideNavigationMenu() {

    val navController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by rememberSaveable { mutableStateOf(ScreenNavItem.Settings.route) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    var currentScreenTitle by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(currentRoute) {
        currentScreenTitle = when (currentRoute) {
            ScreenNavItem.Settings.route -> ScreenNavItem.Settings.title
            ScreenNavItem.ProductDetail.route -> ScreenNavItem.ProductDetail.title
            ScreenNavItem.SearchProductInfo.route -> ScreenNavItem.SearchProductInfo.title
            else -> ScreenNavItem.MainMenu.title
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true, // Разрешить открытие/закрытие жестами
        drawerContent = {
            ModalDrawerSheet {
//                DrawerItem(
//                    label = "Меню",
//                    icon = Icons.Filled.Home,
//                    selected = selectedItem == ScreenNavItem.MainMenu.route,
//                    onClick = {
//                        selectedItem = ScreenNavItem.MainMenu.route
//                        navController.navigate(ScreenNavItem.MainMenu.route)
//                        scope.launch { drawerState.close() }
//                        // Обработка перехода на экран "Меню"
//                    }
//                )
                DrawerItem(
                    label = "Настройки",
                    icon = Icons.Filled.Settings,
                    selected = selectedItem == ScreenNavItem.Settings.route,
                    onClick = {
                        selectedItem = ScreenNavItem.Settings.route
                        navController.navigate(ScreenNavItem.Settings.route)
                        scope.launch { drawerState.close() }
                        // Обработка перехода на экран "Настройки"
                    }
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(currentScreenTitle) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Открыть меню")
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    MainScreen(paddingValues, navController)
                }
            )
        }
    )
}

@Composable
fun DrawerItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val annotatedString = if (selected) {
            AnnotatedString(label, spanStyle = SpanStyle(textDecoration = TextDecoration.Underline))
        } else {
            AnnotatedString(label)
        }

        Icon(imageVector = icon, contentDescription = label)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = annotatedString,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}


