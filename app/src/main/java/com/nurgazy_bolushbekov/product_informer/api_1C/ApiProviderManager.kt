package com.nurgazy_bolushbekov.product_informer.api_1C

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nurgazy_bolushbekov.product_informer.application.DataStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiProviderManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    applicationScope: CoroutineScope
) {
    private val _apiService = MutableStateFlow<Api1C?>(null)
    val apiService: StateFlow<Api1C?> = _apiService.asStateFlow()

    private val _fastApiService = MutableStateFlow<ApiFastapi?>(null)
    val fastApiService: StateFlow<ApiFastapi?> = _fastApiService.asStateFlow()

    init {
        applicationScope.launch {
            dataStoreRepository.networkSettingsFlow.collect { networkSettings ->
                val url = networkSettings.baseUrl
                val user = networkSettings.userName
                val pass = networkSettings.password

                if (url.isNotBlank() && user.isNotBlank()) {
                    _apiService.value = create(user, pass, url)
                } else {
                    _apiService.value = null
                }

                _fastApiService.value = createFastApi()
            }
        }
    }

    fun create(userName: String, password: String, baseUrl: String): Api1C {
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(userName, password))
            .build()

        return Retrofit.Builder()
            .baseUrl(formattedUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api1C::class.java)
    }

    private fun createFastApi(): ApiFastapi {
        val baseUrl = "https://ubuntu.service.kg/"
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        val client = OkHttpClient.Builder().build()

        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl(formattedUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiFastapi::class.java)
    }
}