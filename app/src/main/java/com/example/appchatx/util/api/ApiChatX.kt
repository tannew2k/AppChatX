package com.example.appchatx.util.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

class ApiChatX {

    private val BASE_URL = "https://35e4-14-188-116-171.ngrok-free.app"

    private var token: String = ""
    private lateinit var retrofit: Retrofit
    private lateinit var client: OkHttpClient

    init {
        createClient()
    }

    private fun createClient() {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            if (token.isNotEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
        client = clientBuilder.build()

        val gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    fun setToken(newToken: String) {
        token = newToken
        createClient()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    fun <T> createService(serviceClass: Class<T>, subUrl: String): T {
        val newRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL + subUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(client)
            .build()
        return newRetrofit.create(serviceClass)
    }
}