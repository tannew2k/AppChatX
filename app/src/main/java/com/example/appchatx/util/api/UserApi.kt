package com.example.appchatx.util.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface UserApi {
    @Multipart
    @POST("/api/user/update")
    suspend fun updateUser(
        @Part("name") name: RequestBody,
        @Part("bio") bio: RequestBody,
        @Part("following") following: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<JsonObject>

    // Follow or unfollow a user by ID
    @POST("/api/user/{id}/follow")
    suspend fun toggleFollow(@Path("id") userId: String): Response<JsonObject>

    // Search users by name or email
    @GET("/api/user/search")
    suspend fun searchUsers(@Query("query") query: String): Response<JsonObject>
}