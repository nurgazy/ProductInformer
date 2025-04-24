package com.nurgazy_bolushbekov.product_informer.settings

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiClient
import com.nurgazy_bolushbekov.product_informer.api_1C.ResponseData
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
    var port by mutableIntStateOf(80)
    var publicationName by mutableStateOf("")
    var userName by mutableStateOf("")

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _responseData = MutableStateFlow<ResponseData?>(null)
    val responseData: StateFlow<ResponseData?> = _responseData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    fun checkPing(username: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val baseUrl = "${protocol.name}://${server}:${port}/"
                val url = "$baseUrl${publicationName}/hs/BarcodeInfo/Ping/"

                val api1C = ApiClient.create(username, password, baseUrl)
                val response = api1C.ping(url)

                if (response.isSuccessful) {

                    _responseData.value = ResponseData(response.code(),
                        response.body()?.string().toString()
                    )

                } else {
                    _responseData.value = ResponseData(response.code(), "", response.message())
                }
            }catch (e: Exception){
                _responseData.value = ResponseData(0, "", e.message.toString())
            }finally {
                _isLoading.value = false
            }
        }
    }

}

