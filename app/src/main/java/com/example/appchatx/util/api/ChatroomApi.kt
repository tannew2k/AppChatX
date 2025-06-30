package com.example.appchatx.util.api

import com.example.appchatx.util.api.response.ChatroomCreateResponse
import com.example.appchatx.util.api.response.ChatroomDetailResponse
import com.example.appchatx.util.api.response.ChatroomResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatroomApi {
    @GET("/api/chatrooms")
    suspend fun getChatrooms(): Response<ChatroomResponse>

    @GET("/api/chatrooms/{id}")
    suspend fun getChatroom(@Path("id") id: Int): Response<ChatroomDetailResponse>

    @FormUrlEncoded
    @POST("/api/chatrooms")
    suspend fun createChatroom(
        @Field("name") name: String?
    ): Response<ChatroomCreateResponse>
}