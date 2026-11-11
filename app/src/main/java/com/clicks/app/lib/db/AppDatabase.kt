package com.clicks.app.lib.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.clicks.app.feature.cart.data.CartItemDao
import com.clicks.app.feature.cart.model.CartItemEntity
import com.clicks.app.feature.favorites.data.FavoriteProductDao
import com.clicks.app.feature.favorites.model.FavoriteProductEntity
import com.clicks.app.feature.auth.model.Authentication
import com.clicks.app.feature.auth.model.VerificationCode
import com.clicks.app.feature.auth.repository.AuthenticationDao
import com.clicks.app.feature.auth.repository.VerificationCodeDao

@Database(
    entities = [
        Authentication::class,
        VerificationCode::class,
        CartItemEntity::class,
        FavoriteProductEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authenticationDao(): AuthenticationDao
    abstract fun verificationCodeDao(): VerificationCodeDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun favoriteProductDao(): FavoriteProductDao

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

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS favorite_products (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        categoryId TEXT,
                        price REAL NOT NULL,
                        rating REAL,
                        reviewCount INTEGER NOT NULL,
                        tag TEXT,
                        imageUrl TEXT,
                        description TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val columns = mutableSetOf<String>()
                db.query("PRAGMA table_info(favorite_products)").use { cursor ->
                    val nameColumn = cursor.getColumnIndexOrThrow("name")
                    while (cursor.moveToNext()) {
                        columns.add(cursor.getString(nameColumn))
                    }
                }

                if ("categoryName" in columns && "category" !in columns) {
                    db.execSQL("ALTER TABLE favorite_products RENAME TO favorite_products_legacy")
                    createFavoriteProductsTable(db)
                    db.execSQL(
                        """
                        INSERT INTO favorite_products (
                            id, name, category, categoryId, price, rating,
                            reviewCount, tag, imageUrl, description
                        )
                        SELECT
                            id, name, categoryName, categoryId, price, rating,
                            reviewCount, tag, imageUrl, description
                        FROM favorite_products_legacy
                        """.trimIndent()
                    )
                    db.execSQL("DROP TABLE favorite_products_legacy")
                } else if (columns.isEmpty()) {
                    createFavoriteProductsTable(db)
                }
            }
        }

        private fun createFavoriteProductsTable(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS favorite_products (
                    id TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    category TEXT NOT NULL,
                    categoryId TEXT,
                    price REAL NOT NULL,
                    rating REAL,
                    reviewCount INTEGER NOT NULL,
                    tag TEXT,
                    imageUrl TEXT,
                    description TEXT NOT NULL
                )
                """.trimIndent()
            )
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
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
