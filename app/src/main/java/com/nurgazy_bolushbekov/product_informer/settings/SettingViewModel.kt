package com.nurgazy_bolushbekov.product_informer.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurgazy_bolushbekov.product_informer.api_1C.ApiRepository
import com.nurgazy_bolushbekov.product_informer.application.DataStoreRepository
import com.nurgazy_bolushbekov.product_informer.utils.CryptoManager
import com.nurgazy_bolushbekov.product_informer.utils.ParseInputStringToIntHelper
import com.nurgazy_bolushbekov.product_informer.utils.ResultFetchData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class Protocol{
    HTTP, HTTPS
}

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    private lateinit var apiRepository: ApiRepository

    val protocol: StateFlow<Protocol> = dataStoreRepository.protocol.asStateFlow()
    val server: StateFlow<String> = dataStoreRepository.serverUrl.asStateFlow()
    val port: StateFlow<Int> = dataStoreRepository.port.asStateFlow()
    val publicationName: StateFlow<String> = dataStoreRepository.publicationName.asStateFlow()
    val userName: StateFlow<String> = dataStoreRepository.userName.asStateFlow()
    val password: StateFlow<String> = dataStoreRepository.password.asStateFlow()
    private val baseUrl: StateFlow<String> = dataStoreRepository.baseUrl.asStateFlow()
    val isAllSpecifications: StateFlow<Boolean> = dataStoreRepository.isAllSpecifications.asStateFlow()

    private val _protocolError = MutableStateFlow<String?>(null)
    private val _serverError = MutableStateFlow<String?>(null)
    private val _portError = MutableStateFlow<String?>(null)
    private val _publicationNameError = MutableStateFlow<String?>(null)
    private val _userNameError = MutableStateFlow<String?>(null)
    private val _passwordError = MutableStateFlow<String?>(null)

    private val _isLoading = MutableStateFlow(false)

    private val _pingResult = MutableStateFlow<ResultFetchData<String>?>(null)
    val pingResult: StateFlow<ResultFetchData<String>?> = _pingResult.asStateFlow()

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
        dataStoreRepository.changeProtocol(value)
        changePort(if (protocol.value == Protocol.HTTP) 80 else 443)
    }

    fun onChangeServer(value: String){
        dataStoreRepository.changeServerUrl(value)
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
        dataStoreRepository.changePort(value)
    }

    fun onChangePublicationName(value: String){
        dataStoreRepository.changePublicationName(value)
        validatePublicationName()
        changeFormValid()
    }

    fun onChangeUserName(value: String){
       dataStoreRepository.changeUserName(value)
        validateUserName()
        changeFormValid()
    }

    fun onChangePassword(newPassword: String) {
        dataStoreRepository.changePassword(newPassword)
        validatePasswd()
        changeFormValid()
    }

    fun onChangeIsAllSpecifications(value: Boolean){
        dataStoreRepository.changeIsAllSpecifications(value)
    }


    // Handling of button presses
    fun onCheckBtnPress(){
        validateForm()
        handleAlertData()
        if (_isFormValid.value){
            closeAlertDialog()
            apiRepository = SettingRepositoryImpl(userName.value, password.value, baseUrl.value)
            checkPing()
        }else{
            openAlertDialog()
        }
    }

    fun onReadyBtnPress(){
        validateForm()
        handleAlertData()
        if (_isFormValid.value){
            closeAlertDialog()
            saveSettingsData()
        }else{
            openAlertDialog()
        }
    }

    private fun saveSettingsData(){
        viewModelScope.launch {
            dataStoreRepository.saveProtocol(protocol.value.name)
        }
        viewModelScope.launch {
            dataStoreRepository.saveServerUrl(server.value)
        }
        viewModelScope.launch {
            dataStoreRepository.savePort(port.value)
        }
        viewModelScope.launch {
            dataStoreRepository.savePublicationName(publicationName.value)
        }
        viewModelScope.launch {
            dataStoreRepository.saveUserName(userName.value)
        }
        viewModelScope.launch {
            val (encryptedPassword, iv) = CryptoManager.encrypt(password.value)
            dataStoreRepository.saveEncryptedPassword(encryptedPassword, iv)
        }
        viewModelScope.launch {
            dataStoreRepository.saveIsAllSpecifications(isAllSpecifications.value)
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

    private fun openAlertDialog(){
        _showDialog.value = true
    }

    fun closeAlertDialog(){
        _showDialog.value = false
    }


    //Work with api
    private fun checkPing() {
        viewModelScope.launch {
            _pingResult.value = ResultFetchData.Loading
            when(val result = apiRepository.ping()){
                is ResultFetchData.Error -> {
                    _pingResult.value = result
                    _alertText.value = result.exception.message.toString()
                    openAlertDialog()
                    _isLoading.value = false
                }
                ResultFetchData.Loading -> { _isLoading.value = true }
                is ResultFetchData.Success -> {
                    _pingResult.value = result
                    _alertText.value = "Соединение установлено, приложение готово к работе!"
                    openAlertDialog()
                    _isLoading.value = false
                }
            }
        }
    }
}

