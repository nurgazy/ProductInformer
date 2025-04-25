package com.nurgazy_bolushbekov.product_informer

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nurgazy_bolushbekov.product_informer.settings.Protocol
import com.nurgazy_bolushbekov.product_informer.settings.SettingViewModel
import com.nurgazy_bolushbekov.product_informer.settings.SettingsViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val owner = LocalViewModelStoreOwner.current

            owner?.let {
                val viewModel: SettingViewModel = viewModel(
                    it,
                    "SettingViewModel",
                    SettingsViewModelFactory(LocalContext.current.applicationContext as Application)
                )

                MainScreen(viewModel)
            }

        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainScreen(vm: SettingViewModel){

    Column(Modifier.fillMaxWidth()) {
        ConnectionSettingsRow()
        ProtocolRow(vm)
        ServerRow(vm)
        PortRow(vm)
        PublicationNameRow(vm)
        UserNameRow(vm)
        PasswdRow(vm)
        ButtonRow(vm)
        PingRow(vm)
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
                value = vm.protocol.name,
                onValueChange = { vm.changeProtocol(it) },
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
                            vm.protocol = protocol
                            vm.changePort(if (vm.protocol == Protocol.HTTP) "80" else "443")
                            expanded = false
                        })
                }
            }
        }

    }
}

@Composable
private fun ServerRow(vm: SettingViewModel) {

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
            value = vm.server,
            onValueChange = { vm.changeServer(it) },
            singleLine = true,
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(fontSize = 18.sp),
        )
    }
}

@Composable
private fun PortRow(vm: SettingViewModel) {

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
            value = vm.port.toString(),
            onValueChange = { vm.changePort(it) },
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
}

@Composable
private fun PublicationNameRow(vm: SettingViewModel) {
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
            value = vm.publicationName,
            onValueChange = { vm.changePublicationName(it) },
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
    }
}

@Composable
private fun UserNameRow(vm: SettingViewModel) {
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
            value = vm.userName,
            onValueChange = { vm.changeUserName(it) },
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

    }
}

@Composable
private fun PasswdRow(vm: SettingViewModel) {

    val password by vm.password.collectAsState()

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
                onValueChange = vm::updatePassword,
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
private fun ButtonRow(vm: SettingViewModel) {

    val password by vm.password.collectAsState()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { vm.checkPing(vm.userName, password)},
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        ) {
            Text(stringResource(R.string.btn_check_text), fontSize = 25.sp)
        }
        Button(
            onClick = {
                vm.saveSettingsData()
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

    val responseData by vm.responseData.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    if (isLoading){
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    if (responseData != null) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = responseData?.message.toString(),
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }
}
