package com.example.appchatx.util

import com.example.appchatx.models.User
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.AuthApi
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserUtil {
    companion object {
        var token: String? = null
        var user: User? = null
    }

    suspend fun getCurrentUser(token: String?): User? {
        return withContext(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(token.orEmpty()) }
                val response = api.createService(AuthApi::class.java).getInfo()

                if (response.isSuccessful) {
                    val json: JsonObject? = response.body()
                    val userJson = json?.getAsJsonObject("user")

                    val userModel = User(
                        id = userJson?.get("id")?.asString ?: "",
                        name = userJson?.get("name")?.asString ?: "",
                        email = userJson?.get("email")?.asString ?: "",
                        imageUrl = userJson?.get("image_url")?.asString ?: "",
                        bio = userJson?.get("bio")?.asString ?: "",
                        following = mutableListOf()
                    )

                    user = userModel
                    return@withContext userModel
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext null
        }
    }
}
