package com.nurgazy_bolushbekov.product_informer.api_1C

import com.nurgazy_bolushbekov.product_informer.application.DataStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiProviderManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val applicationScope: CoroutineScope
) {
    private val _apiService = MutableStateFlow<Api1C?>(null)
    val apiService: StateFlow<Api1C?> = _apiService.asStateFlow()

    init {
        applicationScope.launch {
            dataStoreRepository.networkSettingsFlow.collect { networkSettings ->
                if (networkSettings.userName.isNotBlank() && networkSettings.password.isNotBlank() && networkSettings.baseUrl.isNotBlank()) {
                    _apiService.value = create(networkSettings.userName, networkSettings.password, networkSettings.baseUrl)
                } else {
                    _apiService.value = null
                }
            }
        }
    }

    fun create(userName: String, password: String, baseUrl: String): Api1C {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(userName, password))
            .build()

        val instance: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance.create(Api1C::class.java)
    }
}