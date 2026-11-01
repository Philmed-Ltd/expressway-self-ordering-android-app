package com.clicks.app.lib.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.clicks.app.feature.auth.model.Authentication
import com.clicks.app.feature.auth.model.VerificationCode
import com.clicks.app.feature.auth.repository.AuthenticationDao
import com.clicks.app.feature.auth.repository.VerificationCodeDao

@Database(entities = [Authentication::class, VerificationCode::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authenticationDao(): AuthenticationDao
    abstract fun verificationCodeDao(): VerificationCodeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clicks_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
