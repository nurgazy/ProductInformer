package com.nurgazy_bolushbekov.product_informer.settings_page

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nurgazy_bolushbekov.product_informer.R
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem

@Composable
fun SettingScreen(navController: NavController){

    val vm: SettingViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity,
        factory = SettingsViewModelFactory(LocalContext.current.applicationContext as Application)
    )


    Column(Modifier.fillMaxWidth()) {
        ConnectionSettingsRow()
        ProtocolRow(vm)
        ServerRow(vm)
        PortRow(vm)
        PublicationNameRow(vm)
        UserNameRow(vm)
        PasswdRow(vm)
        ButtonRow(vm, navController)
        PingRow(vm)
        ShowAlertDialog(vm)
    }
}

@Composable
private fun ConnectionSettingsRow() {
    Row(
        Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.connection_settings_text),
            modifier = Modifier
                .padding(5.dp)
                .weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProtocolRow(vm: SettingViewModel) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    val protocol by vm.protocol.collectAsStateWithLifecycle()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.protocol_text),
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        )

        ExposedDropdownMenuBox(
            modifier = Modifier.weight(2f),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = protocol.name,
                onValueChange = { vm.onChangeProtocol(it) },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .padding(5.dp)
                    .menuAnchor(MenuAnchorType.PrimaryEditable)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                vm.protocolList.forEach { protocol ->
                    DropdownMenuItem(text = { Text(text = protocol.name) },
                        onClick = {
                            vm.onChangeProtocol(protocol.name)
                            expanded = false
                        })
                }
            }
        }

    }
}

@Composable
private fun ServerRow(vm: SettingViewModel) {

    val server by vm.server.collectAsStateWithLifecycle()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.server_text),
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        )
        OutlinedTextField(
            value = server,
            onValueChange = { vm.onChangeServer(it) },
            singleLine = true,
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
    }
}

@Composable
private fun PortRow(vm: SettingViewModel) {

    val port by vm.port.collectAsStateWithLifecycle()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.port_text),
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        )

        OutlinedTextField(
            value = port.toString(),
            onValueChange = { vm.onChangePort(it) },
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
}

@Composable
private fun PublicationNameRow(vm: SettingViewModel) {

    val publicationName by vm.publicationName.collectAsStateWithLifecycle()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.publication_name_text),
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        )
        OutlinedTextField(
            value = publicationName,
            onValueChange = { vm.onChangePublicationName(it) },
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
    }
}

@Composable
private fun UserNameRow(vm: SettingViewModel) {

    val userName by vm.userName.collectAsStateWithLifecycle()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.username_text),
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        )
        OutlinedTextField(
            value = userName,
            onValueChange = vm::onChangeUserName,
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

    }
}

@Composable
private fun PasswdRow(vm: SettingViewModel) {

    val password by vm.password.collectAsStateWithLifecycle()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.passwd_text),
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        )
        OutlinedTextField(
            value = password,
            onValueChange = vm::onChangePassword,
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
private fun ButtonRow(vm: SettingViewModel, navController: NavController) {

    val isFormValid by vm.isFormValid.collectAsStateWithLifecycle()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { vm.onCheckBtnPress() },
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        ) {
            Text(stringResource(R.string.btn_check_text), fontSize = 25.sp)
        }
        Button(
            onClick = {
                vm.onReadyBtnPress()
                if (isFormValid) {
                    navController.navigate(ScreenNavItem.MainMenu.route)
                }

            },
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        ) {
            Text(stringResource(R.string.btn_ready_text), fontSize = 25.sp)
        }
    }

}

@Composable
private fun PingRow(vm: SettingViewModel) {

    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val checkResponse by vm.checkResponse.collectAsStateWithLifecycle()

    if (isLoading){
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    if (checkResponse.isNotBlank()){
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = checkResponse,
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }

}

@Composable
private fun ShowAlertDialog(vm:SettingViewModel) {

    val alertText by vm.alertText.collectAsStateWithLifecycle()
    val isFormValid by vm.isFormValid.collectAsStateWithLifecycle()

    val showDialog = rememberSaveable { mutableStateOf(false) }
    showDialog.value = !isFormValid

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                vm.changeFormValid(true)
                showDialog.value = false
            },
            text = {
                Text(alertText)
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.changeFormValid(true)
                    showDialog.value = false
                }) {
                    Text("OK")
                }
            }
        )
    }
}