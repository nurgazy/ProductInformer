package com.nurgazy_bolushbekov.product_informer.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import com.nurgazy_bolushbekov.product_informer.application.App
import com.nurgazy_bolushbekov.product_informer.utils.CryptoManager
import com.nurgazy_bolushbekov.product_informer.utils.ParseInputStringToIntHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Protocol{
    HTTP, HTTPS
}

class SettingViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var apiRepository: ApiRepository
    private val connectSettingsPrefRep = (application as App).connectionSettingsPrefRep

    val protocolList = Protocol.entries.toTypedArray()

    val protocol: StateFlow<Protocol> = connectSettingsPrefRep.protocol.asStateFlow()
    val server: StateFlow<String> = connectSettingsPrefRep.serverUrl.asStateFlow()
    val port: StateFlow<Int> = connectSettingsPrefRep.port.asStateFlow()
    val publicationName: StateFlow<String> = connectSettingsPrefRep.publicationName.asStateFlow()
    val userName: StateFlow<String> = connectSettingsPrefRep.userName.asStateFlow()
    val password: StateFlow<String> = connectSettingsPrefRep.password.asStateFlow()
    private val baseUrl = connectSettingsPrefRep.baseUrl.asStateFlow()

    private val _protocolError = MutableStateFlow<String?>(null)
    private val _serverError = MutableStateFlow<String?>(null)
    private val _portError = MutableStateFlow<String?>(null)
    private val _publicationNameError = MutableStateFlow<String?>(null)
    private val _userNameError = MutableStateFlow<String?>(null)
    private val _passwordError = MutableStateFlow<String?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _checkResponse = MutableStateFlow("")
    val checkResponse: StateFlow<String> = _checkResponse.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    private val _alertText = MutableStateFlow("")
    val alertText: StateFlow<String> = _alertText.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()


    // Init data
    init {
        initData()
    }

    private fun initData() {
        changePort(if (protocol.value == Protocol.HTTP) 80 else 443)
        validateForm()
    }


    //Change form data
    fun onChangeProtocol(value: String){
        connectSettingsPrefRep.changeProtocol(value)
        changePort(if (protocol.value == Protocol.HTTP) 80 else 443)
    }

    fun onChangeServer(value: String){
        connectSettingsPrefRep.changeServerUrl(value)
        validateServer()
        changeFormValid()
    }

    fun onChangePort(value: String){
        val portValue = ParseInputStringToIntHelper.parseInputStringToInt(value)
        if (portValue != null) {
            changePort(portValue)
            validatePort()
            changeFormValid()
        }
    }

    private fun changePort(value: Int){
        connectSettingsPrefRep.changePort(value)
    }

    fun onChangePublicationName(value: String){
        connectSettingsPrefRep.changePublicationName(value)
        validatePublicationName()
        changeFormValid()
    }

    fun onChangeUserName(value: String){
       connectSettingsPrefRep.changeUserName(value)
        validateUserName()
        changeFormValid()
    }

    fun onChangePassword(newPassword: String) {
        connectSettingsPrefRep.changePassword(newPassword)
        validatePasswd()
        changeFormValid()
    }


    // Handling of button presses
    fun onCheckBtnPress(){
        validateForm()
        handleAlertData()
        changeShowDialog()
        if (_isFormValid.value){
            apiRepository = SettingRepositoryImpl(userName.value, password.value, baseUrl.value)
            checkPing()
        }
    }

    private fun changeShowDialog() {
        _showDialog.value =
            (_serverError.value != null || _protocolError.value != null || _portError.value != null
                    || _publicationNameError.value != null || _userNameError.value != null || _passwordError.value != null)
    }

    fun onReadyBtnPress(){
        validateForm()
        handleAlertData()
        changeShowDialog()
        if (_isFormValid.value){
            saveSettingsData()
        }
    }

    private fun saveSettingsData(){
        viewModelScope.launch {
            connectSettingsPrefRep.saveProtocol(protocol.value.name)
        }
        viewModelScope.launch {
            connectSettingsPrefRep.saveServerUrl(server.value)
        }
        viewModelScope.launch {
            connectSettingsPrefRep.savePort(port.value)
        }
        viewModelScope.launch {
            connectSettingsPrefRep.savePublicationName(publicationName.value)
        }
        viewModelScope.launch {
            connectSettingsPrefRep.saveUserName(userName.value)
        }
        viewModelScope.launch {
            val (encryptedPassword, iv) = CryptoManager.encrypt(password.value)
            connectSettingsPrefRep.saveEncryptedPassword(encryptedPassword, iv)
        }
    }


    //Validate form data
    private fun validateForm() {
        validateServer()
        validatePort()
        validatePublicationName()
        validateUserName()
        validatePasswd()
        changeFormValid()
    }

    private fun validateServer() {
        _serverError.value = if (server.value.isBlank()) "Не заполнен адрес сервера!" else null
    }

    private fun validatePort() {
        _portError.value = if (port.value == 0) "Не заполнен порт!" else null
    }

    private fun validatePublicationName() {
        _publicationNameError.value = if (publicationName.value.isBlank()) "Не заполнено имя публикации!" else null
    }

    private fun validateUserName() {
        _userNameError.value = if (userName.value.isBlank()) "Не заполнен имя пользователя!" else null
    }

    private fun validatePasswd() {
        _passwordError.value = if (password.value.isBlank()) "Не заполнен пароль!" else null
    }


    //Handling of alert dialog
    private fun changeFormValid(){
        _isFormValid.value = (_serverError.value == null && _protocolError.value == null && _portError.value == null
                && _publicationNameError.value == null && _userNameError.value == null && _passwordError.value == null)
    }

    private fun handleAlertData() {
        if (!_isFormValid.value)
            generateAlertText()
    }

    private fun generateAlertText() {
        _alertText.value = ""
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

    fun onPressAlertDialogBtn(value: Boolean){
        _showDialog.value = value
    }


    //Work with api
    private fun checkPing() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _checkResponse.value = apiRepository.ping()
            } catch (e: Exception){
                _checkResponse.value = "Сообщение об ошибке: ${e.message.toString()}"
                Log.d("ProductInformer", _checkResponse.value)
            }finally {
                _isLoading.value = false
            }
        }
    }
}

