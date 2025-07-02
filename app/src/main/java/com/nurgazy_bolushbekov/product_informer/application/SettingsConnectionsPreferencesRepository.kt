package com.nurgazy_bolushbekov.product_informer.application

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nurgazy_bolushbekov.product_informer.settings.Protocol
import com.nurgazy_bolushbekov.product_informer.utils.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settingsProductInformer")

class SettingsConnectionsPreferencesRepository(private val context: Context) {

    companion object{
        val PROTOCOL_KEY = stringPreferencesKey("protocol_key")
        val SERVER_URL_KEY = stringPreferencesKey("server_url_key")
        val PORT_KEY = stringPreferencesKey("port_key")
        val PUBLICATION_NAME_KEY = stringPreferencesKey("publication_name_key")
        val USER_NAME_KEY = stringPreferencesKey("user_name_key")
        val ENCRYPTED_PASSWORD_KEY = stringPreferencesKey("encrypted_password")
        val ENCRYPTED_IV_KEY = stringPreferencesKey("encrypted_iv")
    }

    val protocol = MutableStateFlow(Protocol.HTTP)
    val serverUrl = MutableStateFlow("")
    val port = MutableStateFlow(80)
    val publicationName = MutableStateFlow("")
    val userName = MutableStateFlow("")
    val password = MutableStateFlow("")
    val baseUrl = MutableStateFlow("")

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            context.dataStore.data.map {
                preferences -> preferences[PROTOCOL_KEY] ?: Protocol.HTTP
            }.
            collect { value ->
                protocol.value = Protocol.valueOf(value.toString())
                changeBaseUrl()
            }
        }

        repositoryScope.launch {
            context.dataStore.data.map {
                preferences -> preferences[SERVER_URL_KEY] ?: ""
            }
            .collect { value ->
                serverUrl.value = value
                changeBaseUrl()
            }
        }

        repositoryScope.launch {
            context.dataStore.data.map {
                preferences -> preferences[PORT_KEY] ?: "0"
            }
            .collect { value ->
                port.value = value.toInt()
                changeBaseUrl()
            }
        }

        repositoryScope.launch {
            context.dataStore.data.map {
                preferences -> preferences[PUBLICATION_NAME_KEY] ?: ""
            }
            .collect{ value ->
                publicationName.value = value
                changeBaseUrl()
            }
        }

        repositoryScope.launch {
            context.dataStore.data.map {
                preferences -> preferences[USER_NAME_KEY] ?: ""
            }.collect{ value ->
                userName.value = value
                changeBaseUrl()
            }
        }

        repositoryScope.launch {
            password.value = loadPassword()
        }

        changePort(if (protocol.value == Protocol.HTTP) 80 else 443)
        changeBaseUrl()
    }

    fun cancelScope() {
        repositoryScope.cancel()
    }

    suspend fun saveProtocol(protocol: String) {
        context.dataStore.edit { preferences ->
            preferences[PROTOCOL_KEY] = protocol
        }
    }

    fun changeProtocol(value: String){
        protocol.value = if (value.isBlank()) Protocol.HTTP else Protocol.valueOf(value)
        changeBaseUrl()
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_URL_KEY] = url
        }
    }

    fun changeServerUrl(value: String){
        serverUrl.value = value
        changeBaseUrl()
    }

    suspend fun savePort(port: Int) {
        context.dataStore.edit { preferences ->
            preferences[PORT_KEY] = port.toString()
        }
    }

    fun changePort(value: Int){
        port.value = value
        changeBaseUrl()
    }

    suspend fun savePublicationName(publicationName: String) {
        context.dataStore.edit { preferences ->
            preferences[PUBLICATION_NAME_KEY] = publicationName
        }
    }

    fun changePublicationName(value: String){
        publicationName.value = value
        changeBaseUrl()
    }

    suspend fun saveUserName(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
        }
    }

    fun changeUserName(value: String){
        userName.value = value
    }

    private suspend fun loadPassword(): String {
        val prefs = context.dataStore.data.first()
        val encryptedPassword = prefs[ENCRYPTED_PASSWORD_KEY]
        val iv = prefs[ENCRYPTED_IV_KEY]
        var decrypted = ""

        if (encryptedPassword != null && iv != null) {
            decrypted = CryptoManager.decrypt(encryptedPassword, iv)
        }
        return decrypted
    }

    suspend fun saveEncryptedPassword(encryptedPassword: String, iv: String) {
        context.dataStore.edit { preferences ->
            preferences[ENCRYPTED_PASSWORD_KEY] = encryptedPassword
            preferences[ENCRYPTED_IV_KEY] = iv
        }
    }

    fun changePassword(value: String){
        password.value = value
    }

    private fun changeBaseUrl(){
        baseUrl.value =
            "${protocol.value.name}://${serverUrl.value}:${port.value}/${publicationName.value}/"
    }
}