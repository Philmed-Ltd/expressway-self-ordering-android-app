package com.clicks.app.feature.auth.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clicks.app.feature.auth.model.VerificationCode

@Dao
interface VerificationCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSync(code: VerificationCode)

    @Query("SELECT * FROM verification_codes LIMIT 1")
    fun selectSync(): VerificationCode?

    @Query("DELETE FROM verification_codes")
    fun delete()
}
