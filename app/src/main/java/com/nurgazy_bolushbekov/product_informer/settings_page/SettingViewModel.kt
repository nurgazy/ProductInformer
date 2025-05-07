package com.nurgazy_bolushbekov.product_informer.settings_page

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiClient
import com.nurgazy_bolushbekov.product_informer.api_1C.ResponseData
import com.nurgazy_bolushbekov.product_informer.utils.CryptoManager
import com.nurgazy_bolushbekov.product_informer.utils.ParseInputStringToIntHelper
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Protocol{
    HTTP, HTTPS
}

class SettingViewModel(application: Application): AndroidViewModel(application) {

    private val dataStoreManager = SettingDataStore(application)

    val protocolList = Protocol.entries.toTypedArray()

    private val _protocol = MutableStateFlow(Protocol.HTTP)
    val protocol: StateFlow<Protocol> = _protocol.asStateFlow()

    private val _protocolError = MutableStateFlow<String?>(null)
    val protocolError: StateFlow<String?> = _protocolError.asStateFlow()

    private val _server = MutableStateFlow("")
    val server: StateFlow<String> = _server.asStateFlow()

    private val _serverError = MutableStateFlow<String?>(null)
    val serverError: StateFlow<String?> = _serverError.asStateFlow()

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

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _alertText = MutableStateFlow("")
    val alertText: StateFlow<String> = _alertText.asStateFlow()


    init {
        loadSettingsData()
        changePort(if (_protocol.value == Protocol.HTTP) 80 else 443)
    }


    fun changeProtocol(value: String){
        _protocol.value = if (value.isBlank()) Protocol.HTTP else Protocol.valueOf(value)
        changePort(if (_protocol.value == Protocol.HTTP) 80 else 443)
    }

    fun changeServer(value: String){
        _server.value = value
        validateServer()

    }

    private fun validateServer() {
        _serverError.update {
            if (_server.value.isBlank()) {
                "Не заполнен адрес сервера!"
            } else {
                null
            }
        }
    }

    fun handlePort(value: String){
        val portValue = ParseInputStringToIntHelper.parseInputStringToInt(value)
        if (portValue != null) {
            changePort(portValue)
        }
    }

    private fun changePort(value: Int){
        _port.value = value
    }

    fun changePublicationName(value: String){
        _publicationName.value = value
    }

    fun changeUserName(value: String){
        _userName.value = value
    }

    fun changePassword(newPassword: String) {
        _password.value = newPassword
    }

    private fun loadSettingsData() {
        viewModelScope.launch {
            dataStoreManager.getProtocol.collect { value ->
                if (value != null) {
                    changeProtocol(value.toString())
                }
            }
        }

        viewModelScope.launch {
            dataStoreManager.getServerUrl.collect { value -> changeServer(value.toString()) }
        }

        viewModelScope.launch {
            dataStoreManager.getPort.collect { value -> changePort(value.toInt()) }
        }

        viewModelScope.launch {
            dataStoreManager.getPublicationName.collect { value -> changePublicationName(value) }
        }

        viewModelScope.launch {
            dataStoreManager.getUserName.collect { value -> changeUserName(value) }
        }

        viewModelScope.launch {
            changePassword(dataStoreManager.loadPassword())
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

    private fun checkPing() {
        _isLoading.value = true
        var responseText = ""
        viewModelScope.launch {
            try {
                val baseUrl = "${_protocol.value.name}://${_server.value}:${_port.value}/"
                val url = "$baseUrl${_publicationName.value}/hs/BarcodeInfo/Ping/"

                val api1C = ApiClient.create(_userName.value, _password.value, baseUrl)
                val response = api1C.ping(url)

                if (response.isSuccessful) {

                    responseText = response.body()?.string().toString()
                    _responseData.value = ResponseData(response.code(), responseText)
                    Log.d(
                        "ProductInformer", "Loading data is successful. Response code:" +
                                " ${_responseData.value!!.httpCode}. Response message: ${_responseData.value!!.message}"
                    )

                } else {

                    responseText =
                        "Код ошибки: ${response.code()}. Сообщение об ошибке: ${response.message()}"
                    _responseData.value = ResponseData(response.code(), responseText)
                    Log.d(
                        "ProductInformer",
                        "Loading data is failed. Error code: ${response.code()}. Error message: ${response.message()}"
                    )
                }

            }catch (e: TimeoutCancellationException){

                responseText = "Ошибка подключение к серверу: Превышено врем ожидания"
                _responseData.value = ResponseData(0, responseText)
                Log.d("ProductInformer", "Error loading data. Error message: $responseText")

            } catch (e: Exception){

                responseText = "Сообщение об ошибке: ${e.message.toString()}"
                _responseData.value = ResponseData(0, responseText)
                Log.d("ProductInformer", "Error loading data. Error message: ${e.message.toString()}")

            }finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateForm() {
        validateServer()
    }

    fun onCheckBtnPress(){
        validateForm()
        if (_serverError.value == null && _protocolError.value == null){
            _showDialog.value = false
        }else{

            _showDialog.value = true
            _alertText.value = ""
            if (_protocolError.value != null) {
                _alertText.value += _protocolError.value + "\n"
            }
            if(_serverError.value != null){
                _alertText.value += _serverError.value + "\n"
            }
        }

        if (!_showDialog.value){
            checkPing()
        }
    }

    fun changeShowDialog(value: Boolean){
        _showDialog.value = value
    }

    fun onReadyBtnPress(){
        validateForm()
        if (_serverError.value == null && _protocolError.value == null){
            _showDialog.value = false
        }else{

            _showDialog.value = true
            _alertText.value = ""
            if (_protocolError.value != null) {
                _alertText.value += _protocolError.value + "\n"
            }
            if(_serverError.value != null){
                _alertText.value += _serverError.value + "\n"
            }
        }
    }
}

