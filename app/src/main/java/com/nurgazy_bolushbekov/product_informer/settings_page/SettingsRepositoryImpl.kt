package com.nurgazy_bolushbekov.product_informer.settings_page

import com.nurgazy_bolushbekov.product_informer.api_1C.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SettingsRepositoryImpl(username: String, password: String, baseUrl: String) : SettingsRepository {

    private val apiService = ApiClient.create(username, password, baseUrl)

    override fun ping(): Flow<String?> = flow{
        emit(apiService.ping().toString())
    }.catch {error->
        emit("Ошибка при получении сообщения: ${error.message}")
    }.flowOn(Dispatchers.IO)

}