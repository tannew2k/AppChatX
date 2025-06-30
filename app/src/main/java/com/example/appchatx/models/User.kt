package com.example.appchatx.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    var following: MutableList<String> = mutableListOf(),
    val bio: String = "",
    @SerializedName("image_url")
    val imageUrl: String = ""
)
