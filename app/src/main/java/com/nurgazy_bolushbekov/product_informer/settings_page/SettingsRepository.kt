package com.nurgazy_bolushbekov.product_informer.settings_page

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun ping(): Flow<String?>
}