package com.plin.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.plin.domain.models.Task
import com.plin.domain.models.Friend
import com.plin.data.FriendDao

/**
 * Room handle for Plin's local SQLite database (`plin.db`).
 *
 * Defines which tables exist and provides access to [TaskDao].
 * The actual data file lives on the device; this class opens and manages it.
 *
 * Schema changes should bump [version] and add a Migration that keeps data
 * (usually ALTER TABLE … ADD COLUMN … DEFAULT …).
 */
@Database(entities = [Task::class, Friend::class], version = 10, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PlinDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun friendDao(): FriendDao

    companion object {
        @Volatile
        private var instance: PlinDatabase? = null

        fun getInstance(context: Context): PlinDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PlinDatabase::class.java,
                    "plin.db",
                )
                    .addMigrations(
                        PlinMigrations.MIGRATION_5_6,
                        PlinMigrations.MIGRATION_6_7,
                        PlinMigrations.MIGRATION_7_8,
                        PlinMigrations.MIGRATION_8_9,
                        PlinMigrations.MIGRATION_9_10
                    )
                    // Pre-v5 schemas changed too much; only wipe those old DBs.
                    .fallbackToDestructiveMigrationFrom(1, 2, 3, 4)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
