package com.nurgazy_bolushbekov.product_informer.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.utils.CryptoManager
import com.nurgazy_bolushbekov.product_informer.utils.ParseInputStringToIntHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Protocol{
    HTTP, HTTPS
}

class SettingViewModel(application: Application): ViewModel() {

    private val dataStoreManager = SettingDataStore(application)

    val protocolList = Protocol.entries.toTypedArray()

    var protocol by mutableStateOf(Protocol.HTTP)
    var server by mutableStateOf("")
    var port by mutableIntStateOf(0)
    var publicationName by mutableStateOf("")
    var userName by mutableStateOf("")

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    init {
        getSettingsData()
    }

    fun changeProtocol(value: String){
        protocol = Protocol.valueOf(value)
    }

    fun changeServer(value: String){
        server = value
    }

    fun changePort(value: String){
        val portValue = ParseInputStringToIntHelper.parseInputStringToInt(value)
        if (portValue != null) {
            port = portValue
        }
    }

    fun changePublicationName(value: String){
        publicationName = value
    }

    fun changeUserName(value: String){
        userName = value
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    private fun getSettingsData() {
        viewModelScope.launch {
            dataStoreManager.getProtocol.collect { value ->
                if (value != null) {
                    protocol = if (value.isNotEmpty()) Protocol.valueOf(value.toString()) else Protocol.HTTP
                }
            }
        }

        viewModelScope.launch {
            dataStoreManager.getServerUrl.collect { value -> server = value.toString() }
        }

        viewModelScope.launch {
            dataStoreManager.getPort.collect { value -> port = value.toInt() }
        }

        viewModelScope.launch {
            dataStoreManager.getPublicationName.collect { value -> publicationName = value }
        }

        viewModelScope.launch {
            dataStoreManager.getUserName.collect { value -> userName = value }
        }

        viewModelScope.launch {
            _password.value = dataStoreManager.loadPassword()
        }

    }

    fun saveSettingsData(){
        viewModelScope.launch { dataStoreManager.saveProtocol(protocol.name) }
        viewModelScope.launch { dataStoreManager.saveServerUrl(server) }
        viewModelScope.launch { dataStoreManager.savePort(port) }
        viewModelScope.launch { dataStoreManager.savePublicationName(publicationName) }
        viewModelScope.launch { dataStoreManager.saveUserName(userName) }
        viewModelScope.launch {
            val (encryptedPassword, iv) = CryptoManager.encrypt(_password.value)
            dataStoreManager.saveEncryptedPassword(encryptedPassword, iv)
        }
    }

}

