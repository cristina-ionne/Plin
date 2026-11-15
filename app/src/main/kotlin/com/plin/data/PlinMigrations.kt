package com.plin.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room schema migrations. Prefer additive changes (ADD COLUMN with DEFAULT)
 * so existing tasks on device are kept.
 */
object PlinMigrations {
    /**
     * Adds [Task.points] with default 1 for existing rows.
     */
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE tasks ADD COLUMN points INTEGER NOT NULL DEFAULT 1",
            )
        }
    }

    /**
     * Adds [Task.assignedWeek]. Empty values are backfilled from [Task.createdAt] on next app open.
     */
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE tasks ADD COLUMN assignedWeek TEXT NOT NULL DEFAULT ''",
            )
        }
    }

    /**
     * Replaces status enum with [Task.completed] boolean.
     * COMPLETED → true; PENDING and ARCHIVED → false.
     */
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE tasks_new (
                    id INTEGER NOT NULL,
                    instanceNumber INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    completed INTEGER NOT NULL,
                    category TEXT NOT NULL,
                    points INTEGER NOT NULL,
                    isWeekly INTEGER NOT NULL,
                    assignedWeek TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    PRIMARY KEY(id, instanceNumber)
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                INSERT INTO tasks_new (
                    id, instanceNumber, title, completed, category,
                    points, isWeekly, assignedWeek, createdAt
                )
                SELECT
                    id,
                    instanceNumber,
                    title,
                    CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END,
                    category,
                    points,
                    isWeekly,
                    assignedWeek,
                    createdAt
                FROM tasks
                """.trimIndent(),
            )
            db.execSQL("DROP TABLE tasks")
            db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
        }
    }

    /**
     * Adds [Task.completedAt] for ordering completed tasks by completion time.
     */
    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE tasks ADD COLUMN completedAt INTEGER",
            )
        }
    }

    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS friends (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    frequency INTEGER NOT NULL
                )
                """.trimIndent(),
            )
        }
    }

    /**
     * Unifies develop and add-friend v10 schemas into v11:
     * - tasks.friendId (if missing)
     * - tasks.duration (if missing; present on develop UI, absent on add-friend)
     * - friends → UUID id + name + frequency
     *
     * Source v10 may be either:
     * - develop: friends(id INTEGER, name, frequency), duration present, no friendId
     * - add-friend: friends(id TEXT, friendName), friendId present, no duration
     */
    val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            if (!hasColumn(db, table = "tasks", column = "friendId")) {
                db.execSQL(
                    "ALTER TABLE tasks ADD COLUMN friendId TEXT NOT NULL DEFAULT ''",
                )
            }
            if (!hasColumn(db, table = "tasks", column = "duration")) {
                db.execSQL(
                    "ALTER TABLE tasks ADD COLUMN duration INTEGER NOT NULL DEFAULT $DEFAULT_DURATION",
                )
            }

            db.execSQL("DROP TABLE IF EXISTS friends_new")
            db.execSQL(
                """
                CREATE TABLE friends_new (
                    id TEXT NOT NULL,
                    name TEXT NOT NULL,
                    frequency INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent(),
            )

            when {
                hasColumn(db, table = "friends", column = "friendName") -> {
                    // add-friend pool: keep existing UUID ids, default frequency
                    db.execSQL(
                        """
                        INSERT INTO friends_new (id, name, frequency)
                        SELECT id, friendName, $DEFAULT_FREQUENCY
                        FROM friends
                        """.trimIndent(),
                    )
                }
                hasColumn(db, table = "friends", column = "name") -> {
                    // develop pool: regenerate UUID ids, keep name/frequency
                    db.execSQL(
                        """
                        INSERT INTO friends_new (id, name, frequency)
                        SELECT lower(hex(randomblob(16))), name, frequency
                        FROM friends
                        """.trimIndent(),
                    )
                }
            }

            db.execSQL("DROP TABLE IF EXISTS friends")
            db.execSQL("ALTER TABLE friends_new RENAME TO friends")
        }
    }

    private const val DEFAULT_FREQUENCY = 3
    private const val DEFAULT_DURATION = 1

    private fun hasColumn(db: SupportSQLiteDatabase, table: String, column: String): Boolean {
        db.query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            if (nameIndex < 0) {
                return false
            }
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIndex) == column) {
                    return true
                }
            }
        }
        return false
    }
}
