package com.nurgazy_bolushbekov.product_informer.api_1C

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor(private val username: String, private val password: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        // Создание учетных данных Basic-аутентификации
        val credential = Credentials.basic(username, password, Charsets.UTF_8)

        // Добавление заголовка Authorization
        builder.addHeader("Authorization", credential)

        val newRequest = builder.build()
        return chain.proceed(newRequest)
    }
}