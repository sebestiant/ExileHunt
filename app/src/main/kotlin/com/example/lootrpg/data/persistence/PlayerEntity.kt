package com.example.lootrpg.data.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "player")
internal data class PlayerEntity(
    @PrimaryKey val slot: Int = 1,
    val playerId: String,
    val name: String,
    val level: Int,
    val currentXp: Long,
    val gold: Long,
    val attack: Int,
    val defense: Int,
    val maxHealth: Int,
    val nextHuntAtEpochMillis: Long? = null,
    @ColumnInfo(defaultValue = "0") val introductionAcknowledged: Boolean = false,
    @ColumnInfo(defaultValue = "0") val victories: Long = 0,
    @ColumnInfo(defaultValue = "0") val defeats: Long = 0,
)
