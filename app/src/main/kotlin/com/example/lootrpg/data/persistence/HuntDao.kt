package com.example.lootrpg.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
internal interface HuntDao {
    @Query("SELECT * FROM recent_hunt ORDER BY sequence DESC LIMIT 10")
    suspend fun recent(): List<HuntEntity>

    @Insert
    suspend fun insert(hunt: HuntEntity)

    @Query("DELETE FROM recent_hunt WHERE sequence NOT IN (SELECT sequence FROM recent_hunt ORDER BY sequence DESC LIMIT 10)")
    suspend fun prune()
}
