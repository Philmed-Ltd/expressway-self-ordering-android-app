package com.clicks.app.lib.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.clicks.app.feature.cart.data.CartItemDao
import com.clicks.app.feature.cart.data.CartItemEntity
import com.clicks.app.feature.auth.model.Authentication
import com.clicks.app.feature.auth.model.VerificationCode
import com.clicks.app.feature.auth.repository.AuthenticationDao
import com.clicks.app.feature.auth.repository.VerificationCodeDao

@Database(
    entities = [Authentication::class, VerificationCode::class, CartItemEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authenticationDao(): AuthenticationDao
    abstract fun verificationCodeDao(): VerificationCodeDao
    abstract fun cartItemDao(): CartItemDao

    companion object {
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS cart_items (
                        productId TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        categoryId TEXT,
                        price REAL NOT NULL,
                        rating REAL,
                        reviewCount INTEGER NOT NULL,
                        tag TEXT,
                        imageUrl TEXT,
                        description TEXT NOT NULL,
                        quantity INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

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
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
