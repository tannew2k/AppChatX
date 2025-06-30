// CreateChatRequest.kt
package com.example.appchatx.util.api.request

data class CreateChatRequest(
    val text: String,
    val author_id: Int,
    val chatroom_id: Int,
    val timestamp: Long
)
