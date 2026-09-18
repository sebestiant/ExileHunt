package com.example.lootrpg.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FoundationMarker::class], version = 1, exportSchema = true)
internal abstract class LootRpgDatabase : RoomDatabase() {
    abstract fun foundationDao(): FoundationDao
}
