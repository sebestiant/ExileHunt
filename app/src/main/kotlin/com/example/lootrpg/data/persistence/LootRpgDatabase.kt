package com.example.lootrpg.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PlayerEntity::class, HuntEntity::class], version = 3, exportSchema = true)
internal abstract class LootRpgDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun huntDao(): HuntDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE player ADD COLUMN nextHuntAtEpochMillis INTEGER")
                db.execSQL("ALTER TABLE player ADD COLUMN introductionAcknowledged INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player ADD COLUMN victories INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player ADD COLUMN defeats INTEGER NOT NULL DEFAULT 0")
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS recent_hunt (
                        sequence INTEGER NOT NULL PRIMARY KEY,
                        monsterId TEXT NOT NULL, monsterName TEXT NOT NULL, monsterLevel INTEGER NOT NULL,
                        monsterDescription TEXT NOT NULL, outcome TEXT NOT NULL, rounds INTEGER NOT NULL,
                        damageDealt INTEGER NOT NULL, damageTaken INTEGER NOT NULL, roundLimitReached INTEGER NOT NULL,
                        experienceGained INTEGER NOT NULL, goldGained INTEGER NOT NULL,
                        occurredAtEpochMillis INTEGER NOT NULL, nextHuntAtEpochMillis INTEGER NOT NULL,
                        levelUp_fromLevel INTEGER, levelUp_toLevel INTEGER,
                        levelUp_beforeAttack INTEGER, levelUp_beforeDefense INTEGER, levelUp_beforeHealth INTEGER,
                        levelUp_afterAttack INTEGER, levelUp_afterDefense INTEGER, levelUp_afterHealth INTEGER
                    )
                """.trimIndent())
            }
        }

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
