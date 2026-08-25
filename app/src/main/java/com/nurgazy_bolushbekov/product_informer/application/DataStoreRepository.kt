package com.nurgazy_bolushbekov.product_informer.application

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nurgazy_bolushbekov.product_informer.data_classes.NetworkSettings
import com.nurgazy_bolushbekov.product_informer.utils.CryptoManager
import com.nurgazy_bolushbekov.product_informer.utils.Protocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val applicationScope: CoroutineScope
) {

    companion object{
        private val PROTOCOL_KEY = stringPreferencesKey("protocol_key")
        private val SERVER_URL_KEY = stringPreferencesKey("server_url_key")
        private val PORT_KEY = stringPreferencesKey("port_key")
        private val PUBLICATION_NAME_KEY = stringPreferencesKey("publication_name_key")
        private val USER_NAME_KEY = stringPreferencesKey("user_name_key")
        private val ENCRYPTED_PASSWORD_KEY = stringPreferencesKey("encrypted_password")
        private val ENCRYPTED_IV_KEY = stringPreferencesKey("encrypted_iv")
        private val IS_ALL_SPECIFICATIONS_KEY = stringPreferencesKey("is_all_specifications")
        private val IS_CYCLIC_SCANNING_KEY = stringPreferencesKey("is_continuous_scanning")
        private val LAST_UPDATE_CHECK_KEY = stringPreferencesKey("last_update_check")
        private val IS_APP_SERVER_KEY = stringPreferencesKey("is_app_server")
    }

    val protocol = MutableStateFlow(Protocol.HTTP)
    val serverUrl = MutableStateFlow("")
    val port = MutableStateFlow(80)
    val publicationName = MutableStateFlow("")
    val userName = MutableStateFlow("")
    val password = MutableStateFlow("")
    val isFullSpecifications = MutableStateFlow(false)
    val baseUrl = MutableStateFlow("")
    val isCyclicScanning = MutableStateFlow(false)
    val isAppServer = MutableStateFlow(false)

    val networkSettingsFlow = dataStore.data.map { preferences ->

        val userName = preferences[USER_NAME_KEY]
        val password = loadPassword()

        val protocol = preferences[PROTOCOL_KEY]?: Protocol.HTTP.name
        val serverUrl = preferences[SERVER_URL_KEY]?: ""
        val port = preferences[PORT_KEY]?: "0"
        val publicationName = preferences[PUBLICATION_NAME_KEY]?: ""
        val isFullSpecifications = preferences[IS_ALL_SPECIFICATIONS_KEY]?: "false"
        val isCyclicScanning = preferences[IS_CYCLIC_SCANNING_KEY]?: "false"

        val baseUrl = "${protocol}://${serverUrl}:${port}/${publicationName}/"

        NetworkSettings(
            protocol = Protocol.valueOf(protocol),
            serverUrl = serverUrl,
            port = port.toInt(),
            publicationName = publicationName,
            isFullSpecification = isFullSpecifications.toBoolean(),
            baseUrl = baseUrl,
            userName = userName?:"",
            password = password,
            isCyclicScanning = isCyclicScanning.toBoolean()
        )
    }

    init {
        applicationScope.launch {
            dataStore.data.map { preferences -> preferences[PROTOCOL_KEY] ?: Protocol.HTTP }.
                collect { value -> protocol.value = Protocol.valueOf(value.toString()) }
            changePort(if (protocol.value == Protocol.HTTP) 80 else 443)
            changeBaseUrl()
        }

        applicationScope.launch {
            dataStore.data.map { preferences -> preferences[SERVER_URL_KEY] ?: "" }.
            collect { value -> serverUrl.value = value }
            changeBaseUrl()
        }

        applicationScope.launch {
            dataStore.data.map { preferences -> preferences[PORT_KEY] ?: "0" }.
            collect { value -> port.value = value.toInt() }
            changeBaseUrl()
        }

        applicationScope.launch {
            dataStore.data.map { preferences -> preferences[PUBLICATION_NAME_KEY] ?: "" }.
            collect { value -> publicationName.value = value }
            changeBaseUrl()
        }

        applicationScope.launch {
            dataStore.data.map { preferences -> preferences[USER_NAME_KEY] ?: "" }.
            collect { value -> userName.value = value }
        }

        applicationScope.launch {
            password.value = loadPassword()
        }

        applicationScope.launch {
            dataStore.data.map { preferences -> preferences[IS_ALL_SPECIFICATIONS_KEY] ?: "false" }.
            collect { value -> isFullSpecifications.value = value.toBoolean() }
        }

        applicationScope.launch {
            dataStore.data.map { preferences -> preferences[IS_CYCLIC_SCANNING_KEY] ?: "false" }.
            collect { value -> isCyclicScanning.value = value.toBoolean() }
        }

        applicationScope.launch {
            dataStore.data.map { preferences -> preferences[IS_APP_SERVER_KEY] ?: "false" }
                .collect { value -> isAppServer.value = value.toBoolean() }
        }
    }

    private suspend fun loadPassword(): String {
        val prefs = dataStore.data.first()
        val encryptedPassword = prefs[ENCRYPTED_PASSWORD_KEY]
        val encryptedIv = prefs[ENCRYPTED_IV_KEY]
        var decrypted = ""

        if (encryptedPassword != null && encryptedIv != null) {
            decrypted = CryptoManager.decrypt(encryptedPassword, encryptedIv)
        }
        return decrypted
    }

    fun changeProtocol(value: String){
        protocol.value = if (value.isBlank()) Protocol.HTTP else Protocol.valueOf(value)
        changeBaseUrl()
    }

    fun changeServerUrl(value: String){
        serverUrl.value = value
        changeBaseUrl()
    }

    fun changePort(value: Int){
        port.value = value
        changeBaseUrl()
    }

    fun changePublicationName(value: String){
        publicationName.value = value
        changeBaseUrl()
    }

    fun changeUserName(value: String){
        userName.value = value
    }

    fun changePassword(value: String){
        password.value = value
    }

    fun changeIsCyclicScanning(value: Boolean) {
        isCyclicScanning.value = value
    }

    fun changeIsFullSpecifications(value: Boolean){
        isFullSpecifications.value = value
    }

    private fun changeBaseUrl(){
        baseUrl.value =
            "${protocol.value.name}://${serverUrl.value}:${port.value}/${publicationName.value}/"
    }

    fun changeIsAppServer(value: Boolean) {
        isAppServer.value = value
    }

    suspend fun saveAllSettings(
        protocol: String,
        serverUrl: String,
        port: Int,
        publicationName: String,
        userName: String,
        encryptedPassword: Pair<String, String>,
        isFullSpecs: Boolean,
        isCyclicScanning: Boolean,
        isAppServer: Boolean
    ) {
        dataStore.edit{ preferences ->
            preferences[PROTOCOL_KEY] = protocol
            preferences[SERVER_URL_KEY] = serverUrl
            preferences[PORT_KEY] = port.toString()
            preferences[PUBLICATION_NAME_KEY] = publicationName
            preferences[USER_NAME_KEY] = userName
            preferences[ENCRYPTED_PASSWORD_KEY] = encryptedPassword.first
            preferences[ENCRYPTED_IV_KEY] = encryptedPassword.second
            preferences[IS_ALL_SPECIFICATIONS_KEY] = isFullSpecs.toString()
            preferences[IS_CYCLIC_SCANNING_KEY] = isCyclicScanning.toString()
            preferences[IS_APP_SERVER_KEY] = isAppServer.toString()
        }
    }

    suspend fun shouldCheckUpdate(): Boolean {
        val prefs = dataStore.data.first()
        val lastCheck = prefs[LAST_UPDATE_CHECK_KEY]?.toLong() ?: 0L
        val currentTime = System.currentTimeMillis()

        return (currentTime - lastCheck) > 24 * 60 * 60 * 1000
    }

    suspend fun markUpdateChecked() {
        dataStore.edit { preferences ->
            preferences[LAST_UPDATE_CHECK_KEY] = System.currentTimeMillis().toString()
        }
    }

}