package com.clicks.app.feature.auth.model

import androidx.room.Entity
import com.clicks.app.lib.model.BaseEntity
import java.util.Date
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Entity(tableName = "authentication")
class Authentication : BaseEntity() {
    var expiryTime: Date = Date(0)
    var authToken: String = ""

    fun hasExpired(): Boolean = this.expiryTime.before(Date())

    companion object {
        fun parseIsoDate(dateStr: String?): Date? {
            if (dateStr.isNullOrBlank()) return null
            return try {
                val localDateTime = LocalDateTime.parse(
                    dateStr.removeSuffix("Z").removeSuffix("z"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                )
                Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }
}
