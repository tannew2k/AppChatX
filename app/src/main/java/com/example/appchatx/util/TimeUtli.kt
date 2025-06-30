package com.example.appchatx.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone // ✅ Add this

object TimeUtli {
    fun formatUnixTime(unixTime: Long): String {
        return try {
            val date = Date(unixTime * 1000)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            sdf.format(date)
        } catch (e: Exception) {
            "Unknown time"
        }
    }

    fun parseDateToUnix(dateString: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            val date = format.parse(dateString)
            date?.time?.div(1000) ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
