package com.example.lootrpg.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PlayerEntity::class], version = 2, exportSchema = true)
internal abstract class LootRpgDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `player` (
                        `slot` INTEGER NOT NULL, `playerId` TEXT NOT NULL,
                        `name` TEXT NOT NULL, `level` INTEGER NOT NULL,
                        `currentXp` INTEGER NOT NULL, `gold` INTEGER NOT NULL,
                        `attack` INTEGER NOT NULL, `defense` INTEGER NOT NULL,
                        `maxHealth` INTEGER NOT NULL, PRIMARY KEY(`slot`)
                    )
                """.trimIndent())
                db.execSQL("DROP TABLE IF EXISTS `foundation_marker`")
            }
        }
    }
}
