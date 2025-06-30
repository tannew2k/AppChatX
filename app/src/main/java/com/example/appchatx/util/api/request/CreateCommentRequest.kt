package com.example.appchatx.util.api.request


data class CreateCommentRequest(
    val content: String,
    val post_id: String
)

