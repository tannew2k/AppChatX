package com.example.appchatx.util.api.request

data class UpdateUserRequest(
    val name: String,
    val bio: String,
    val image_url: String,
    val following: List<String>
)

