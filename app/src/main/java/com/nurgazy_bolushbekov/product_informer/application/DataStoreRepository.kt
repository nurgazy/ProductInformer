package com.nurgazy_bolushbekov.product_informer.application

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nurgazy_bolushbekov.product_informer.settings.Protocol
import com.nurgazy_bolushbekov.product_informer.utils.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val PROTOCOL_KEY = stringPreferencesKey("protocol_key")
    private val SERVER_URL_KEY = stringPreferencesKey("server_url_key")
    private val PORT_KEY = stringPreferencesKey("port_key")
    private val PUBLICATION_NAME_KEY = stringPreferencesKey("publication_name_key")
    private val USER_NAME_KEY = stringPreferencesKey("user_name_key")
    private val ENCRYPTED_PASSWORD_KEY = stringPreferencesKey("encrypted_password")
    private val ENCRYPTED_IV_KEY = stringPreferencesKey("encrypted_iv")
    private val IS_ALL_SPECIFICATIONS_KEY = stringPreferencesKey("is_all_specifications")

    val protocol = MutableStateFlow(Protocol.HTTP)
    val serverUrl = MutableStateFlow("")
    val port = MutableStateFlow(80)
    val publicationName = MutableStateFlow("")
    val userName = MutableStateFlow("")
    val password = MutableStateFlow("")
    val isAllSpecifications = MutableStateFlow(false)
    val baseUrl = MutableStateFlow("")

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            dataStore.data.map { preferences -> preferences[PROTOCOL_KEY] ?: Protocol.HTTP }.
                collect { value -> protocol.value = Protocol.valueOf(value.toString()) }
        }

        repositoryScope.launch {
            dataStore.data.map { preferences -> preferences[SERVER_URL_KEY] ?: "" }.
            collect { value -> serverUrl.value = value }
        }

        repositoryScope.launch {
            dataStore.data.map { preferences -> preferences[PORT_KEY] ?: "0" }.
            collect { value -> port.value = value.toInt() }
        }

        repositoryScope.launch {
            dataStore.data.map { preferences -> preferences[PUBLICATION_NAME_KEY] ?: "" }.
            collect { value -> publicationName.value = value }
        }

        repositoryScope.launch {
            dataStore.data.map { preferences -> preferences[USER_NAME_KEY] ?: "" }.
            collect { value -> userName.value = value }
        }

        repositoryScope.launch {
            password.value = loadPassword()
        }

        repositoryScope.launch {
            dataStore.data.map { preferences -> preferences[IS_ALL_SPECIFICATIONS_KEY] ?: "false" }.
            collect { value -> isAllSpecifications.value = value.toBoolean() }
        }

        changePort(if (protocol.value == Protocol.HTTP) 80 else 443)
        changeBaseUrl()
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

    suspend fun saveProtocol(protocol: String) {
        dataStore.edit { preferences ->
            preferences[PROTOCOL_KEY] = protocol
        }
    }

    fun changeProtocol(value: String){
        protocol.value = if (value.isBlank()) Protocol.HTTP else Protocol.valueOf(value)
        changeBaseUrl()
    }

    suspend fun saveServerUrl(serverUrl: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_URL_KEY] = serverUrl
        }
    }

    fun changeServerUrl(value: String){
        serverUrl.value = value
        changeBaseUrl()
    }

    suspend fun savePort(port: Int) {
        dataStore.edit { preferences ->
            preferences[PORT_KEY] = port.toString()
        }
    }

    fun changePort(value: Int){
        port.value = value
        changeBaseUrl()
    }

    suspend fun savePublicationName(publicationName: String) {
        dataStore.edit { preferences ->
            preferences[PUBLICATION_NAME_KEY] = publicationName
        }
    }

    fun changePublicationName(value: String){
        publicationName.value = value
        changeBaseUrl()
    }

    suspend fun saveUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
        }
    }

    fun changeUserName(value: String){
        userName.value = value
    }

    suspend fun saveEncryptedPassword(encryptedPassword: String, encryptedIv: String) {
        dataStore.edit { preferences ->
            preferences[ENCRYPTED_PASSWORD_KEY] = encryptedPassword
            preferences[ENCRYPTED_IV_KEY] = encryptedIv
        }
    }

    fun changePassword(value: String){
        password.value = value
    }

    suspend fun saveIsAllSpecifications(isAllSpecifications: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_ALL_SPECIFICATIONS_KEY] = isAllSpecifications.toString()
        }
    }

    fun changeIsAllSpecifications(value: Boolean){
        isAllSpecifications.value = value
    }

    private fun changeBaseUrl(){
        baseUrl.value =
            "${protocol.value.name}://${serverUrl.value}:${port.value}/${publicationName.value}/"
    }

}