package com.nurgazy_bolushbekov.product_informer.api_1C

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: Api1C by lazy {
        Retrofit.Builder()
            .baseUrl("https://your.api.url/") // 🔁 замените на реальный адрес
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api1C::class.java)
    }
}