package com.nurgazy_bolushbekov.product_informer.settings_page

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiClient
import com.nurgazy_bolushbekov.product_informer.api_1C.ResponseData
import com.nurgazy_bolushbekov.product_informer.utils.CryptoManager
import com.nurgazy_bolushbekov.product_informer.utils.ParseInputStringToIntHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Protocol{
    HTTP, HTTPS
}

class SettingViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var settingsRepository: SettingsRepository
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


    private val _protocolError = MutableStateFlow<String?>(null)
    private val _serverError = MutableStateFlow<String?>(null)
    private val _portError = MutableStateFlow<String?>(null)
    private val _publicationNameError = MutableStateFlow<String?>(null)
    private val _userNameError = MutableStateFlow<String?>(null)
    private val _passwordError = MutableStateFlow<String?>(null)


    private var baseUrl = MutableStateFlow("")

    private val _responseData = MutableStateFlow<ResponseData?>(null)
    val responseData: StateFlow<ResponseData?> = _responseData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isFormValid = MutableStateFlow(true)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    private val _alertText = MutableStateFlow("")
    val alertText: StateFlow<String> = _alertText.asStateFlow()


    // Init data
    init {
        loadSettingsData()
        initData()
    }

    private fun loadSettingsData() {
        viewModelScope.launch {
            dataStoreManager.getProtocol.collect { value ->
                if (value != null) {
                    onChangeProtocol(value.toString())
                }
            }
        }

        viewModelScope.launch {
            dataStoreManager.getServerUrl.collect { value -> onChangeServer(value.toString()) }
        }

        viewModelScope.launch {
            dataStoreManager.getPort.collect { value -> changePort(value.toInt()) }
        }

        viewModelScope.launch {
            dataStoreManager.getPublicationName.collect { value -> onChangePublicationName(value) }
        }

        viewModelScope.launch {
            dataStoreManager.getUserName.collect { value -> onChangeUserName(value) }
        }

        viewModelScope.launch {
            onChangePassword(dataStoreManager.loadPassword())
        }

    }

    private fun initData() {
        changeBaseUrl()
        settingsRepository = SettingsRepositoryImpl(_userName.value, _password.value, baseUrl.value)
        changePort(if (_protocol.value == Protocol.HTTP) 80 else 443)
    }

    private fun changeBaseUrl() {
        baseUrl.value =
            "${_protocol.value.name}://${_server.value}:${_port.value}/${_publicationName.value}/"
    }


    //Change form data
    fun onChangeProtocol(value: String){
        _protocol.value = if (value.isBlank()) Protocol.HTTP else Protocol.valueOf(value)
        changePort(if (_protocol.value == Protocol.HTTP) 80 else 443)
        changeBaseUrl()
    }

    fun onChangeServer(value: String){
        _server.value = value
        validateServer()
        changeBaseUrl()
    }

    fun onChangePort(value: String){
        val portValue = ParseInputStringToIntHelper.parseInputStringToInt(value)
        if (portValue != null) {
            changePort(portValue)
            validatePort()
            changeBaseUrl()
        }
    }

    private fun changePort(value: Int){
        _port.value = value
    }

    fun onChangePublicationName(value: String){
        _publicationName.value = value
        validatePublicationName()
        changeBaseUrl()
    }

    fun onChangeUserName(value: String){
        _userName.value = value
        validateUserName()
    }

    fun onChangePassword(newPassword: String) {
        _password.value = newPassword
        validatePasswd()
    }


    // Handling of button presses
    fun onCheckBtnPress(){
        validateForm()
        handleAlertData()

        if (_isFormValid.value){
            checkPing()
        }
    }

    fun onReadyBtnPress(){
        validateForm()
        handleAlertData()
        if (_isFormValid.value){
            saveSettingsData()
        }
    }

    private fun saveSettingsData(){
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


    //Validate form data
    private fun validateForm() {
        validateServer()
        validatePort()
        validatePublicationName()
        validateUserName()
        validatePasswd()
    }

    private fun validateServer() {
        _serverError.value = if (_server.value.isBlank()) "Не заполнен адрес сервера!" else null
    }

    private fun validatePort() {
        _portError.value = if (_port.value == 0) "Не заполнен порт!" else null
    }

    private fun validatePublicationName() {
        _publicationNameError.value = if (_publicationName.value.isBlank()) "Не заполнено имя публикации!" else null
    }

    private fun validateUserName() {
        _userNameError.value = if (_userName.value.isBlank()) "Не заполнен имя пользователя!" else null
    }

    private fun validatePasswd() {
        _passwordError.value = if (_password.value.isBlank()) "Не заполнен пароль!" else null
    }


    //Handling of alert dialog
    fun changeFormValid(value: Boolean){
        _isFormValid.value = value
    }

    private fun handleAlertData() {
        if (_serverError.value == null && _protocolError.value == null && _portError.value == null
            && _publicationNameError.value == null && _userNameError.value == null && _passwordError.value == null) {
            _isFormValid.value = true
        } else {

            _isFormValid.value = false
            generateAlertText()
        }
    }

    private fun generateAlertText() {
        if (_protocolError.value != null) {
            _alertText.value += _protocolError.value + "\n"
        }
        if (_serverError.value != null) {
            _alertText.value += _serverError.value + "\n"
        }
        if (_portError.value != null) {
            _alertText.value += _portError.value + "\n"
        }
        if (_publicationNameError.value != null) {
            _alertText.value += _publicationNameError.value + "\n"
        }
        if (_userNameError.value != null) {
            _alertText.value += _userNameError.value + "\n"
        }
        if (_passwordError.value != null) {
            _alertText.value += _passwordError.value + "\n"
        }
    }


    //Work with api
    private fun checkPing() {
        _isLoading.value = true
        var responseText = ""
        viewModelScope.launch {
            try {

                val api1C = ApiClient.create(_userName.value, _password.value, baseUrl.value)
                val response = api1C.ping()

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
}

