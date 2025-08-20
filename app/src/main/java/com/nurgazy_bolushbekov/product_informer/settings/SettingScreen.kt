package com.nurgazy_bolushbekov.product_informer.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nurgazy_bolushbekov.product_informer.R
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import com.nurgazy_bolushbekov.product_informer.utils.ScreenNavItem
import kotlinx.coroutines.launch

enum class SettingScreenTab(val title: String) {
    SETTING_GENERAL("Основные"),
    SETTING_ADDITTIONAL("Дополнительные"),
}

@Composable
fun SettingScreen(navController: NavController, vm: SettingViewModel = hiltViewModel()){

    BackHandler {
        navController.popBackStack(ScreenNavItem.SearchProductInfo.route, false)
    }

    val tabs = SettingScreenTab.entries.toTypedArray()
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed{ index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(tab.title) }
                )
            }
        }
        HorizontalPager(
            state = pagerState
        ) { page ->
            when (page) {
                0 -> GeneralTab(vm = vm, navController = navController)
                1 -> AdditionalTab(vm = vm)
            }
        }
    }
}

@Composable
fun GeneralTab(vm: SettingViewModel, navController: NavController){
    val scrollState = rememberScrollState()
    val protocol by vm.protocol.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        ProtocolRow(
            protocolName = protocol.name,
            onChangeProtocol = { newProtocol -> vm.onChangeProtocol(newProtocol) })
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
fun AdditionalTab(vm: SettingViewModel){

    val isFullSpecifications by vm.isAllSpecifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(5.dp)
        ) {
            Switch(
                checked = isFullSpecifications,
                onCheckedChange = { vm.onChangeIsAllSpecifications(it) }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Получать остатки и цены по всем характеристикам склада",
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProtocolRow(
    protocolName: String,
    onChangeProtocol: (String) -> Unit) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    val protocolList = Protocol.entries.toTypedArray()

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
                value = protocolName,
                onValueChange = onChangeProtocol,
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .padding(5.dp)
                    .menuAnchor(MenuAnchorType.PrimaryEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                protocolList.forEach { protocol ->
                    DropdownMenuItem(text = { Text(text = protocol.name) },
                        onClick = {
                            onChangeProtocol(protocol.name)
                            expanded = false
                        })
                }
            }
        }

    }
}

@Composable
private fun ServerRow(vm: SettingViewModel) {

    val server by vm.server.collectAsState()

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

    val port by vm.port.collectAsState()

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
            singleLine = true,
            readOnly = true
        )
    }
}

@Composable
private fun PublicationNameRow(vm: SettingViewModel) {

    val publicationName by vm.publicationName.collectAsState()

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
            onValueChange = {
                vm.onChangePublicationName(it)
            },
            modifier = Modifier
                .padding(5.dp)
                .weight(2f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true
        )
    }
}

@Composable
private fun UserNameRow(vm: SettingViewModel) {

    val userName by vm.userName.collectAsState()

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
            singleLine = true
        )

    }
}

@Composable
private fun PasswdRow(vm: SettingViewModel) {

    val password by vm.password.collectAsState()
    var isPasswdVisible by rememberSaveable { mutableStateOf(false) }

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
            visualTransformation = if (isPasswdVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswdVisible = !isPasswdVisible }) {
                    Icon(
                        painter = if (isPasswdVisible) painterResource(R.drawable.passwd_show) else painterResource(R.drawable.passwd_hide),
                        contentDescription = if (isPasswdVisible) "Скрыть пароль" else "Показать пароль",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            singleLine = true
        )
    }
}

@Composable
private fun ButtonRow(vm: SettingViewModel, navController: NavController) {

    val isFormValid by vm.isFormValid.collectAsState()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                vm.onCheckBtnPress()
            },
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        ) {
            Text(stringResource(R.string.btn_check_text))
        }
        Button(
            onClick = {
                vm.onReadyBtnPress()
                if (isFormValid) {
                    navController.navigate(ScreenNavItem.SearchProductInfo.route)
                }

            },
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        ) {
            Text(stringResource(R.string.btn_ready_text))
        }
    }

}

@Composable
private fun PingRow(vm: SettingViewModel) {

    val pingResult by vm.pingResult.collectAsState()

    when(pingResult){

        is ResultFetchData.Error -> {
            val errorData = (pingResult as ResultFetchData.Error).exception.message
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = errorData.toString(),
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }
        ResultFetchData.Loading -> {
            Box(modifier = Modifier.fillMaxSize())
            {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        is ResultFetchData.Success -> {
            val succesData = (pingResult as ResultFetchData.Success).data
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = succesData,
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }

        null -> {}
    }

}

@Composable
private fun ShowAlertDialog(vm:SettingViewModel) {

    val alertText by vm.alertText.collectAsState()
    val showDialog by vm.showDialog.collectAsState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                vm.closeAlertDialog()
            },
            text = {
                Text(alertText)
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.closeAlertDialog()
                }) {
                    Text("OK")
                }
            }
        )
    }
}