package com.example.appchatx.util.api

import com.example.appchatx.util.api.request.LoginRequest
import com.example.appchatx.util.api.request.RegisterRequest
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @Headers("Content-Type: application/json")
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("/api/register")
    suspend fun register(@Body request: RegisterRequest): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("/api/logout")
    suspend fun logout(): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("/api/info")
    suspend fun getInfo(): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("/api/refresh")
    suspend fun refresh(): Response<JsonObject>
}
