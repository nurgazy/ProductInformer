package com.nurgazy_bolushbekov.product_informer.api_1C

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    fun create(username: String, password: String): Api1C {
        val credentials = Credentials.basic(username, password)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", credentials)
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://your-api.com/") // Замени на свой API
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api1C::class.java)
    }
}