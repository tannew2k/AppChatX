// FIXED ChatApi.kt
package com.example.appchatx.util.api

import com.example.appchatx.models.Chat
import com.example.appchatx.util.api.request.CreateChatRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {
    @Headers("Content-Type: application/json")
    @POST("/api/chats")
    suspend fun createChat(@Body chatData: CreateChatRequest): Chat

    @GET("/api/chatrooms/{chatroomId}/chats")
    suspend fun getChatsByRoom(@Path("chatroomId") roomId: Int): List<Chat>
}
