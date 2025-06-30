package com.example.appchatx.models

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("content")
    val text: String = "",

    val author: User = User(),

    @SerializedName("post_id")
    val postId: Int,

    @SerializedName("created_at")
    val timeString: String = "",  // For raw string date

    val time: Long = 0L  // Optional: convert `timeString` to UNIX manually if needed
)
