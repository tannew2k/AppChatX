package com.example.appchatx.models

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    val text: String = "",
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val user: User,
    @SerializedName("comments_count")
    val commentCount: Int,
    val comments: MutableList<Comment> = mutableListOf(),
    val time: Long = 0L,
    @SerializedName("likes")
    val likeList: MutableList<String> = mutableListOf()
)
