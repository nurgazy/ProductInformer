package com.nurgazy_bolushbekov.product_informer.settings

import android.app.Application
import android.util.Log
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

    private val _protocol = MutableStateFlow(Protocol.HTTP)
    val protocol: StateFlow<Protocol> = _protocol.asStateFlow()

    private val _server = MutableStateFlow("")
    val server: StateFlow<String> = _server.asStateFlow()

    private val _port = MutableStateFlow(80)
    val port: StateFlow<Int> = _port.asStateFlow()

    private val _publicationName = MutableStateFlow("")
    val publicationName: StateFlow<String> = _publicationName

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _responseData = MutableStateFlow<ResponseData?>(null)
    val responseData: StateFlow<ResponseData?> = _responseData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    init {
        getSettingsData()
    }


    fun changeProtocol(value: String){
        _protocol.value = Protocol.valueOf(value)
        if (_protocol.value == Protocol.HTTP) {
            _port.value = 80
        }else{
            _port.value = 443
        }
    }

    fun changeServer(value: String){
        _server.value = value
    }

    fun changePort(value: String){
        val portValue = ParseInputStringToIntHelper.parseInputStringToInt(value)
        if (portValue != null) {
            _port.value = portValue
        }
    }

    fun changePublicationName(value: String){
        _publicationName.value = value
    }

    fun changeUserName(value: String){
        _userName.value = value
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    private fun getSettingsData() {
        viewModelScope.launch {
            dataStoreManager.getProtocol.collect { value ->
                if (value != null) {
                    _protocol.value = if (value.isNotEmpty()) Protocol.valueOf(value.toString()) else Protocol.HTTP
                }
            }
        }

        viewModelScope.launch {
            dataStoreManager.getServerUrl.collect { value -> _server.value = value.toString() }
        }

        viewModelScope.launch {
            dataStoreManager.getPort.collect { value -> _port.value = value.toInt() }
        }

        viewModelScope.launch {
            dataStoreManager.getPublicationName.collect { value -> _publicationName.value = value }
        }

        viewModelScope.launch {
            dataStoreManager.getUserName.collect { value -> _userName.value = value }
        }

        viewModelScope.launch {
            _password.value = dataStoreManager.loadPassword()
        }

    }

    fun saveSettingsData(){
        viewModelScope.launch { dataStoreManager.saveProtocol(_protocol.value.name) }
        viewModelScope.launch { dataStoreManager.saveServerUrl(_server.value) }
        viewModelScope.launch { dataStoreManager.savePort(_port.value) }
        viewModelScope.launch { dataStoreManager.savePublicationName(_publicationName.value) }
        viewModelScope.launch { dataStoreManager.saveUserName(_userName.value) }
        viewModelScope.launch {
            val (encryptedPassword, iv) = CryptoManager.encrypt(_password.value)
            dataStoreManager.saveEncryptedPassword(encryptedPassword, iv)
        }
    }

    fun checkPing() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val baseUrl = "${_protocol.value.name}://${_server.value}:${_port.value}/"
                val url = "$baseUrl${_publicationName.value}/hs/BarcodeInfo/Ping/"

                val api1C = ApiClient.create(_userName.value, _password.value, baseUrl)
                val response = api1C.ping(url)

                if (response.isSuccessful) {

                    _responseData.value = ResponseData(response.code(), response.body()?.string().toString())
                    Log.d("ProductInformer", "Loading data is successful. Response code:" +
                            " ${_responseData.value!!.httpCode}. Response message: ${_responseData.value!!.message}")

                } else {
                    _responseData.value = ResponseData(response.code(), "", response.message())
                    Log.d("ProductInformer", "Loading data is failed. Error code: ${response.code()}. Error message: ${response.message()}")
                }
            }catch (e: Exception){
                _responseData.value = ResponseData(0, "", e.message.toString())
                Log.d("ProductInformer", "Error loading data. Error code: 0. Error message: ${e.message.toString()}")
            }finally {
                _isLoading.value = false
            }
        }
    }

}

