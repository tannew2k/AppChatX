package com.example.appchatx.util

import android.content.Context
import androidx.core.content.edit

object SessionUtil {
    private const val PREF_NAME = "chatx_user_session"
    private const val KEY_TOKEN = "token"

    fun saveSession(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit { clear() }
    }

    fun getToken(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_TOKEN, null)
    }
}