package com.example.simbionte.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

@Database(
    entities = [MemoryBlock::class, MemoryRelation::class, ContextWindow::class, ContinuityTrace::class, UserProfile::class],
    version = 9
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun continuityDao(): ContinuityDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "simbionte_db"
                )
                .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS continuity_traces (
                        id TEXT NOT NULL PRIMARY KEY,
                        conversaId TEXT,
                        traceKind TEXT NOT NULL,
                        openedAt INTEGER NOT NULL,
                        lastTouchedAt INTEGER NOT NULL,
                        closedAt INTEGER
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_profile (
                        id TEXT NOT NULL PRIMARY KEY DEFAULT 'default',
                        communicationDepth REAL NOT NULL DEFAULT 0.5,
                        warmthPreference REAL NOT NULL DEFAULT 0.5,
                        curiosityLevel REAL NOT NULL DEFAULT 0.5,
                        avgMessageLength REAL NOT NULL DEFAULT 0,
                        questionRate REAL NOT NULL DEFAULT 0,
                        reflectionRate REAL NOT NULL DEFAULT 0,
                        topicStability REAL NOT NULL DEFAULT 0.5,
                        totalInteractions INTEGER NOT NULL DEFAULT 0,
                        lastActiveTimestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "INSERT OR IGNORE INTO user_profile (id, lastActiveTimestamp) VALUES ('default', ?)",
                    arrayOf(System.currentTimeMillis())
                )
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE user_profile ADD COLUMN sentimentScore REAL NOT NULL DEFAULT 0"
                )
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE user_profile ADD COLUMN userName TEXT")
                database.execSQL("ALTER TABLE user_profile ADD COLUMN userNickname TEXT")
                database.execSQL("ALTER TABLE user_profile ADD COLUMN purpose TEXT")
                database.execSQL("ALTER TABLE user_profile ADD COLUMN characterName TEXT")
                database.execSQL("ALTER TABLE user_profile ADD COLUMN limits TEXT")
                database.execSQL("ALTER TABLE user_profile ADD COLUMN tonePreference TEXT")
                database.execSQL("ALTER TABLE user_profile ADD COLUMN onboardingStep INTEGER NOT NULL DEFAULT -1")
            }
        }
    }
}
