package com.example.appchatx.models

data class Chat(
    val id: Int = 0,
    val text: String = "",
    val time: Long = 0L,
    val author: User = User()
)
