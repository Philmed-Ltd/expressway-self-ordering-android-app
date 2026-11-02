package com.clicks.app.feature.auth.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Entity(tableName = "authentication")
class Authentication {
    @PrimaryKey
    var id: Long = 1
    var expiryTime: Date = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
    var authToken: String = ""

    fun hasExpired(): Boolean = this.expiryTime.before(Date())

    companion object {
        fun parseIsoDate(dateStr: String?): Date {
            val defaultExpiry = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
            if (dateStr.isNullOrBlank()) return defaultExpiry
            val patterns = arrayOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss"
            )
            for (pattern in patterns) {
                try {
                    val sdf = SimpleDateFormat(pattern, Locale.US)
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    sdf.parse(dateStr)?.let {
                        return if (it.before(Date())) defaultExpiry else it
                    }
                } catch (_: Exception) {}
            }
            return defaultExpiry
        }
    }
}
