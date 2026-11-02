package com.clicks.app.feature.auth.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clicks.app.feature.auth.model.Authentication

@Dao
interface AuthenticationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSync(auth: Authentication)

    @Query("SELECT * FROM authentication LIMIT 1")
    fun selectSync(): Authentication?

    @Query("DELETE FROM authentication")
    fun delete()
}
