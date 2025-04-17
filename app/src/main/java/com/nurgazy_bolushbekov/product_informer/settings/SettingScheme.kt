package com.nurgazy_bolushbekov.product_informer.settings

import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingScheme {
    val FIELD_SERVER_URL = stringPreferencesKey("server_url_key")
}