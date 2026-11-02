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
    var expiryTime: Date = Date()
    var authToken: String = ""

    fun hasExpired(): Boolean = this.expiryTime.before(Date())

    companion object {
        fun parseIsoDate(dateStr: String?): Date {
            if (dateStr.isNullOrBlank()) return Date()
            val patterns = arrayOf(
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
                    sdf.parse(dateStr)?.let { return it }
                } catch (_: Exception) {}
            }
            return Date()
        }
    }
}
