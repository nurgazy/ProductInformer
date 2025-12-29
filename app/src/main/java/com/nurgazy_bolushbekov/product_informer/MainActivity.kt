package com.nurgazy_bolushbekov.product_informer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nurgazy_bolushbekov.product_informer.barcode_collection.detail.BarcodeDetailScreen
import com.nurgazy_bolushbekov.product_informer.barcode_collection.list.BarcodeListScreen
import com.nurgazy_bolushbekov.product_informer.product.SharedVM
import com.nurgazy_bolushbekov.product_informer.product.product_detail.ProductDetailScreen
import com.nurgazy_bolushbekov.product_informer.barcode_input.BarcodeInputScreen
import com.nurgazy_bolushbekov.product_informer.settings.SettingScreen
import com.nurgazy_bolushbekov.product_informer.ui.theme.ProductInformerTheme
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductInformerTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SideNavigationMenu()
                }
            }
        }
    }
}

@Composable
fun MainScreen(paddingValues: PaddingValues, navController: NavHostController){

    val sharedVM: SharedVM = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = ScreenNavItem.SearchProductInfo.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(ScreenNavItem.Settings.route) { SettingScreen(navController) }
        composable(ScreenNavItem.SearchProductInfo.route) {
            BarcodeInputScreen(navController, sharedVM = sharedVM)
        }
        composable(route = ScreenNavItem.ProductDetail.route) {
            ProductDetailScreen(navController, sharedVM = sharedVM)
        }
        composable(ScreenNavItem.BarcodeList.route) {
            BarcodeListScreen(navController)
        }
        composable(
            route = ScreenNavItem.BarcodeDetail.route+"?barcodeDocId={barcodeDocId}",
            arguments = listOf(
                navArgument("barcodeDocId") {
                    type = NavType.LongType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val barcodeDocId = backStackEntry.arguments?.getLong("barcodeDocId")?: 0
            BarcodeDetailScreen(navController, barcodeDocId = barcodeDocId)
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
    val currentRoute = currentBackStackEntry?.destination?.route?.substringBefore("?")

    var currentScreenTitle by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(currentRoute) {
        currentScreenTitle = when (currentRoute) {
            ScreenNavItem.Settings.route -> ScreenNavItem.Settings.title
            ScreenNavItem.ProductDetail.route -> ScreenNavItem.ProductDetail.title
            ScreenNavItem.SearchProductInfo.route -> ScreenNavItem.SearchProductInfo.title
            ScreenNavItem.BarcodeList.route -> ScreenNavItem.BarcodeList.title
            ScreenNavItem.BarcodeDetail.route -> ScreenNavItem.BarcodeDetail.title
            else -> ScreenNavItem.MainMenu.title
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true, // Разрешить открытие/закрытие жестами
        drawerContent = {
            ModalDrawerSheet {
                DrawerItem(
                    label = "Настройки",
                    icon = Icons.Filled.Settings,
                    selected = selectedItem == ScreenNavItem.Settings.route,
                    onClick = {
                        selectedItem = ScreenNavItem.Settings.route
                        navController.navigate(ScreenNavItem.Settings.route)
                        scope.launch { drawerState.close() }
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
                },
                bottomBar = { BottomNavigationBar(navController) }
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


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(ScreenNavItem.SearchProductInfo, ScreenNavItem.BarcodeList)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(
        ScreenNavItem.SearchProductInfo,
        ScreenNavItem.BarcodeList
    )
    val showBottomBar = bottomBarScreens.any { it.route == currentRoute }

    if (!showBottomBar) {
        return
    }

    NavigationBar(
        modifier = Modifier.height(56.dp)
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Избегаем создания нескольких экземпляров одного и того же экрана
                        launchSingleTop = true
                        // Сохраняем и восстанавливаем состояние, чтобы пользователь не терял местоположение при переключении
                        restoreState = true
                    }
                }
            )
        }
    }
}

