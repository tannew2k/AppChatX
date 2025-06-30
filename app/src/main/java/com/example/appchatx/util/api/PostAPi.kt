package com.example.appchatx.util.api

import com.example.appchatx.util.api.response.ListPostResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PostApi {
    @Multipart
    @POST("/api/posts")
    suspend fun createPost(
        @Part("text") text: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<JsonObject>

    @GET("/api/posts/{id}")
    suspend fun getPostById(
        @Path("id") postId: Int,
    ): Response<JsonObject>

    @GET("/api/posts")
    suspend fun listPost(): Response<ListPostResponse>

    @POST("/api/posts/{id}/like")
    suspend fun likePost(
        @Path("id") postId: Int
    ): Response<JsonObject>

}