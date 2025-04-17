package com.nurgazy_bolushbekov.product_informer.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nurgazy_bolushbekov.product_informer.utils.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingDataStore(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settingsProductInformer")
        val PROTOCOL_KEY = stringPreferencesKey("protocol_key")
        val SERVER_URL_KEY = stringPreferencesKey("server_url_key")
        val PORT_KEY = stringPreferencesKey("port_key")
        val PUBLICATION_NAME_KEY = stringPreferencesKey("publication_name_key")
        val USER_NAME_KEY = stringPreferencesKey("user_name_key")
        val ENCRYPTED_PASSWORD_KEY = stringPreferencesKey("encrypted_password")
        val ENCRYPTED_IV_KEY = stringPreferencesKey("encrypted_iv")
    }

    val getProtocol: Flow<String?> = context.dataStore.data.map {
            preferences -> preferences[PROTOCOL_KEY] ?: ""
    }

    suspend fun saveProtocol(protocol: String) {
        context.dataStore.edit { preferences ->
            preferences[PROTOCOL_KEY] = protocol
        }
    }

    val getServerUrl: Flow<String?> = context.dataStore.data.map {
        preferences -> preferences[SERVER_URL_KEY] ?: ""
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_URL_KEY] = url
        }
    }

    val getPort: Flow<String> = context.dataStore.data.map {
        preferences -> preferences[PORT_KEY] ?: "0"
    }

    suspend fun savePort(port: Int) {
        context.dataStore.edit { preferences ->
            preferences[PORT_KEY] = port.toString()
        }
    }

    val getPublicationName: Flow<String> = context.dataStore.data.map {
        preferences -> preferences[PUBLICATION_NAME_KEY] ?: ""
    }

    suspend fun savePublicationName(publicationName: String) {
        context.dataStore.edit { preferences ->
            preferences[PUBLICATION_NAME_KEY] = publicationName
        }
    }

    val getUserName: Flow<String> = context.dataStore.data.map {
            preferences -> preferences[USER_NAME_KEY] ?: ""
    }

    suspend fun saveUserName(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
        }
    }

    suspend fun loadPassword(): String {
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

}