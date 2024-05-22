package com.example.invoiceandroidapp.Connection

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun create(): Retrofit {

        val okHttpClient = OkHttpClient
            .Builder()
            .build()

        return Retrofit.Builder()
            .baseUrl("http://192.168.29.109:9092/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

}