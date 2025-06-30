package com.example.appchatx.util.api

import com.example.appchatx.util.api.request.CreateCommentRequest
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CommnetApi {
    @Headers("Content-Type: application/json")
    @POST("/api/comments")
    suspend fun createComment(
        @Body request: CreateCommentRequest
    ): Response<JsonObject>

}