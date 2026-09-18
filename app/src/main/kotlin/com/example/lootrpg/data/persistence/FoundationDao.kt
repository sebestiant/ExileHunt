package com.example.lootrpg.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface FoundationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(marker: FoundationMarker)

    @Query("SELECT * FROM foundation_marker WHERE id = 1")
    suspend fun read(): FoundationMarker?
}
