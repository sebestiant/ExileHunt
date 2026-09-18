package com.example.lootrpg.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
internal abstract class PlayerDao {
    @Query("SELECT * FROM player WHERE slot = 1")
    abstract suspend fun read(): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertIfAbsent(player: PlayerEntity)

    @Update
    abstract suspend fun update(player: PlayerEntity)

    @Transaction
    open suspend fun getOrCreate(initialPlayer: PlayerEntity): PlayerEntity {
        read()?.let { return it }
        insertIfAbsent(initialPlayer)
        return checkNotNull(read()) { "Player initialization did not persist a profile" }
    }
}
