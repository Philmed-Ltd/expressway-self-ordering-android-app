package com.clicks.app.feature.auth.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verification_codes")
class VerificationCode {
    @PrimaryKey
    var id: String = ""
    var message: String = ""
    var createdAt: String = ""
    var expiryTime: String = ""
}
