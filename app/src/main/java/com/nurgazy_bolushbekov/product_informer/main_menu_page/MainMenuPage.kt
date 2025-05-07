package com.nurgazy_bolushbekov.product_informer.main_menu_page

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nurgazy_bolushbekov.product_informer.settings_page.SettingViewModel
import com.nurgazy_bolushbekov.product_informer.settings_page.SettingsViewModelFactory

@Composable
fun MainMenuPage(navController: NavController){

    val vm: SettingViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity,
        factory = SettingsViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    Column(Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        ) {
            Text(text = "Чек прайс", fontSize = 25.sp)
        }
    }
}