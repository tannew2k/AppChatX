package com.example.appchatx.util.api.response

import com.example.appchatx.models.Chat
import com.example.appchatx.models.Chatroom

data class ChatroomResponse(
    val chatrooms: List<Chatroom>
)

data class ChatroomCreateResponse(
    val message: String,
    val chatroom: Chatroom
)

data class ChatroomDetailResponse(
    val chatroom: Chatroom,
    val chats: List<Chat>
)

