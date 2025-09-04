package com.nurgazy_bolushbekov.product_informer.api_1C

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun create(username: String, password: String, baseUrl: String): Api1C {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(username, password))
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