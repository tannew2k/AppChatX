package com.example.appchatx.models

import com.google.gson.annotations.SerializedName
data class Chatroom(
    val id: String,
    val name: String,
    @SerializedName("author_id")
    val authorId: Int,
)
