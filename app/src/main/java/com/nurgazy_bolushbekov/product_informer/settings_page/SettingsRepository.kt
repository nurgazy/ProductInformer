package com.nurgazy_bolushbekov.product_informer.settings_page

interface SettingsRepository {
    suspend fun ping(): String
}